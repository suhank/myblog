[TOC]



# TypeScript 强大的类型别名

TS 有个非常好用的功能就是类型别名。

类型别名会给一个类型起个新名字。类型别名有时和接口很像，但是可以作用于原始值，联合类型，元组以及其它任何你需要手写的类型。

## 一些关键字

使用类型别名可以实现很多复杂的类型，很多复杂的类型别名都需要借助关键字，我们先来了解一下几个常用的关键字：

### extends

`extends` 可以用来继承一个类，也可以用来继承一个 `interface`，但还可以用来判断有条件类型：

```
T extends U ? X : Y;

```

上面的类型意思是，若 `T` 能够赋值给 `U`，那么类型是 `X`，否则为 `Y`。

原理是令 `T'` 和 `U'` 分别为 `T` 和 `U` 的实例，并将所有类型参数替换为 `any`，如果 `T'` 能赋值给 `U'`，则将有条件的类型解析成 `X`，否则为`Y`。

上面的官方解释有点绕，下面举个栗子：

```
type Words = 'a'|'b'|"c";

type W<T> = T extends Words ? true : false;

type WA = W<'a'>; // -> true
type WD = W<'d'>; // -> false

```

`a` 可以赋值给 `Words` 类型，所以 `WA` 为 `true`，而 `d` 不能赋值给 `Words` 类型，所以 `WD` 为 `false`。

### typeof

在 JS 中 `typeof` 可以判断一个变量的基础数据类型，在 TS 中，它还有一个作用，就是获取一个变量的声明类型，如果不存在，则获取该类型的推论类型。

举两个栗子：

```
interface Person {
  name: string;
  age: number;
  location?: string;
}

const jack: Person = { name: 'jack', age: 100 };
type Jack = typeof jack; // -> Person

function foo(x: number): Array<number> {
  return [x];
}

type F = typeof foo; // -> (x: number) => number[]

```

`Jack` 这个类型别名实际上就是 `jack` 的类型 `Person`，而 `F` 的类型就是 TS 自己推导出来的 `foo` 的类型 `(x: number) => number[]`。

### keyof

`keyof` 可以用来取得一个对象接口的所有 key 值：

```
interface Person {
    name: string;
    age: number;
    location?: string;
}

type K1 = keyof Person; // "name" | "age" | "location"
type K2 = keyof Person[];  // "length" | "push" | "pop" | "concat" | ...
type K3 = keyof { [x: string]: Person };  // string | number

```

### in

`in` 可以遍历枚举类型：

```
type Keys = "a" | "b"
type Obj =  {
  [p in Keys]: any
} // -> { a: any, b: any }

```

上面 `in` 遍历 `Keys`，并为每个值赋予 `any` 类型。

### infer

在条件类型语句中, 可以用 `infer` 声明一个类型变量并且对它进行使用，

我们可以用它获取函数的返回类型， 源码如下：

```
type ReturnType<T> = T extends (
  ...args: any[]
) => infer R
  ? R
  : any;

```

其实这里的 `infer R` 就是声明一个变量来承载传入函数签名的返回值类型, 简单说就是用它取到函数返回值的类型方便之后使用。

## 内置类型别名

下面我们看一下 TS 内置的一些类型别名：

### Partial

`Partial` 的作用就是可以将某个类型里的属性全部变为可选项 `?`。

源码：

```
// node_modules/typescript/lib/lib.es5.d.ts

type Partial<T> = {
    [P in keyof T]?: T[P];
};

```

从源码可以看到 `keyof T` 拿到 `T` 所有属性名, 然后 `in` 进行遍历, 将值赋给 `P`, 最后 `T[P]` 取得相应属性的值. 结合中间的 `?`，将所有属性变为可选.

### Required

`Required` 的作用刚好跟 `Partial` 相反，`Partial` 是将所有属性改成可选项，`Required` 则是将所有类型改成必选项，源码如下：

```
// node_modules/typescript/lib/lib.es5.d.ts

type Required<T> = {
    [P in keyof T]-?: T[P];
};

```

其中 `-?` 是代表移除 `?` 这个 modifier 的标识。

与之对应的还有个 `+?` , 这个含义自然与 `-?` 之前相反, 它是用来把属性变成可选项的，`+` 可省略，见 `Partial`。

再拓展一下，除了可以应用于 `?` 这个 modifiers ，还有应用在 `readonly` ，比如 `Readonly`.

### Readonly

这个类型的作用是将传入的属性变为只读选项。

```
// node_modules/typescript/lib/lib.es5.d.ts

type Readonly<T> = {
    readonly [P in keyof T]: T[P];
};

```

给子属性添加 `readonly` 的标识，如果将上面的 `readonly` 改成 `-readonly`， 就是移除子属性的 `readonly` 标识。

### Pick

这个类型则可以将某个类型中的子属性挑出来，变成包含这个类型部分属性的子类型。

源码实现如下:

```
// node_modules/typescript/lib/lib.es5.d.ts

type Pick<T, K extends keyof T> = {
    [P in K]: T[P];
};

```

从源码可以看到 `K` 必须是 `T` 的 key，然后用 `in` 进行遍历, 将值赋给 `P`, 最后 `T[P]` 取得相应属性的值。

### Record

该类型可以将 `K` 中所有的属性的值转化为 `T` 类型，源码实现如下：

```
// node_modules/typescript/lib/lib.es5.d.ts

type Record<K extends keyof any, T> = {
    [P in K]: T;
};

```

可以根据 `K` 中的所有可能值来设置 key，以及 value 的类型，举个例子：

```
type T11 = Record<'a' | 'b' | 'c', Person>; // -> { a: Person; b: Person; c: Person; }

```

### Exclude

`Exclude` 将某个类型中属于另一个的类型移除掉。

源码的实现：

```
// node_modules/typescript/lib/lib.es5.d.ts

type Exclude<T, U> = T extends U ? never : T;

```

以上语句的意思就是 如果 `T` 能赋值给 `U` 类型的话，那么就会返回 `never` 类型，否则返回 `T`，最终结果是将 `T` 中的某些属于 `U` 的类型移除掉，举个例子：

```
type T00 = Exclude<'a' | 'b' | 'c' | 'd', 'a' | 'c' | 'f'>;  // -> 'b' | 'd'

```

可以看到 `T` 是 `'a' | 'b' | 'c' | 'd'` ，然后 `U` 是 `'a' | 'c' | 'f'` ，返回的新类型就可以将 `U` 中的类型给移除掉，也就是 `'b' | 'd'` 了。

### Extract

`Extract` 的作用是提取出 `T` 包含在 `U` 中的元素，换种更加贴近语义的说法就是从 `T` 中提取出 `U`，源码如下：

```
// node_modules/typescript/lib/lib.es5.d.ts

type Extract<T, U> = T extends U ? T : never;

```

以上语句的意思就是 如果 `T` 能赋值给 `U` 类型的话，那么就会返回 `T` 类型，否则返回 `never`，最终结果是将 `T` 和 `U` 中共有的属性提取出来，举个例子：

```
type T01 = Extract<'a' | 'b' | 'c' | 'd', 'a' | 'c' | 'f'>;  // -> 'a' | 'c'

```

可以看到 `T` 是 `'a' | 'b' | 'c' | 'd'` ，然后 `U` 是 `'a' | 'c' | 'f'` ，返回的新类型就可以将 `T` 和 `U` 中共有的属性提取出来，也就是 `'a' | 'c'` 了。

### ReturnType

该类型的作用是获取函数的返回类型。

源码的实现

```
// node_modules/typescript/lib/lib.es5.d.ts

type ReturnType<T extends (...args: any[]) => any> = T extends (...args: any[]) => infer R ? R : any;

```

实际使用的话，就可以通过 `ReturnType` 拿到函数的返回类型，如下的示例：

```
function foo(x: number): Array<number> {
  return [x];
}

type fn = ReturnType<typeof foo>; // -> number[]

```

### ThisType

这个类型是用于指定上下文对象类型的。

```
// node_modules/typescript/lib/lib.es5.d.ts

interface ThisType<T> { }

```

可以看到声明中只有一个接口，没有任何的实现，说明这个类型是在 TS 源码层面支持的，而不是通过类型变换。

这类型怎么用呢，举个例子：

```
interface Person {
    name: string;
    age: number;
}

const obj: ThisType<Person> = {
  dosth() {
    this.name // string
  }
}

```

这样的话，就可以指定 `obj` 里的所有方法里的上下文对象改成 `Person` 这个类型了。

### InstanceType

该类型的作用是获取构造函数类型的实例类型。

源码实现：

```
// node_modules/typescript/lib/lib.es5.d.ts

type InstanceType<T extends new (...args: any[]) => any> = T extends new (...args: any[]) => infer R ? R : any;

```

看一下官方的例子：

```
class C {
    x = 0;
    y = 0;
}

type T20 = InstanceType<typeof C>;  // C
type T21 = InstanceType<any>;  // any
type T22 = InstanceType<never>;  // any
type T23 = InstanceType<string>;  // Error
type T24 = InstanceType<Function>;  // Error

```

### NonNullable

这个类型可以用来过滤类型中的 `null` 及 `undefined` 类型。

源码实现：

```
// node_modules/typescript/lib/lib.es5.d.ts

type NonNullable<T> = T extends null | undefined ? never : T;

```

比如：

```
type T22 = string | number | null;
type T23 = NonNullable<T22>; // -> string | number;

```

### Parameters

该类型可以获得函数的参数类型组成的元组类型。

源码实现：

```
// node_modules/typescript/lib/lib.es5.d.ts

type Parameters<T extends (...args: any[]) => any> = T extends (...args: infer P) => any ? P : never;

```

举个栗子：

```
function foo(x: number): Array<number> {
  return [x];
}

type P = Parameters<typeof foo>; // -> [number]

```

此时 `P` 的真实类型就是 `foo` 的参数组成的元组类型 `[number]`。

### ConstructorParameters

该类型的作用是获得类的参数类型组成的元组类型，源码：

```
// node_modules/typescript/lib/lib.es5.d.ts

type ConstructorParameters<T extends new (...args: any[]) => any> = T extends new (...args: infer P) => any ? P : never;

```

举个栗子：

```
class Person {
  private firstName: string;
  private lastName: string;
  
  constructor(firstName: string, lastName: string) {
      this.firstName = firstName;
      this.lastName = lastName;
  }
}

type P = ConstructorParameters<typeof Person>; // -> [string, string]

```

此时 `P` 就是 `Person` 中 `constructor` 的参数 `firstName` 和 `lastName` 的类型所组成的元组类型 `[string, string]`。

## 自定义类型别名

下面是一些可能会经常用到，但是 TS 没有内置的一些类型别名：

### Omit

有时候我们想要继承某个接口，但是又需要在新接口中将某个属性给 overwrite 掉，这时候通过 `Pick` 和 `Exclude` 就可以组合出来 `Omit`，用来忽略对象某些属性功能：

```
type Omit<T, K> = Pick<T, Exclude<keyof T, K>>;

// 使用
type Foo = Omit<{name: string, age: number}, 'name'> // -> { age: number }

```

### Mutable

将 T 的所有属性的 `readonly` 移除：

```
type Mutable<T> = {
  -readonly [P in keyof T]: T[P]
}

```

### PowerPartial

内置的 Partial 有个局限性，就是只支持处理第一层的属性，如果是嵌套多层的就没有效果了，不过可以如下自定义：

```
type PowerPartial<T> = {
    // 如果是 object，则递归类型
    [U in keyof T]?: T[U] extends object
      ? PowerPartial<T[U]>
      : T[U]
};

```

### Deferred

相同的属性名称，但使值是一个 `Promise`，而不是一个具体的值：

```
type Deferred<T> = {
    [P in keyof T]: Promise<T[P]>;
};

```

### Proxify

为 `T` 的属性添加代理

```
type Proxify<T> = {
    [P in keyof T]: { get(): T[P]; set(v: T[P]): void }
};

```

如有疑问，欢迎斧正！

# 参考

[TypeScript中文网](https://www.tslang.cn/docs/home.html)

[TS 中的内置类型简述](https://wanghx.cn/blog/github/issue13.html)

[TypeScript 一些你可能不知道的工具泛型的使用及其实现](https://www.javascriptcn.com/read-36627.html)







<https://juejin.im/post/5c2f87ce5188252593122c98>
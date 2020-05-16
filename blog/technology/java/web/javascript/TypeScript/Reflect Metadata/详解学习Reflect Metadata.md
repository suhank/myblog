[TOC]



# 详解学习Reflect Metadata

时间: 05/29/2019作者: ll浏览量: 984

## 1.前言

在 ES6 的规范当中，就已经存在 Reflect API 了。简单来说这个 API 的作用就是可以实现对变量操作的函数化，也就是反射。具体的关于这个 API 的内容，可以查看这个Reflect。

在学习一些node库源码时，经常遇到Reflect Metadata，这是Reflect 里面还没有的一个规范。

## 2.什么是Metadata

想必对于其他语言的 Coder 来说，比如说 Java 或者 C#，Metadata 是很熟悉的。最简单的莫过于通过反射来获取类属性上面的批注（在 JS 当中，也就是所谓的装饰器）。从而可以更加优雅的对代码进行控制。

而 JS 现在有装饰器，虽然现在还在 Stage2 阶段。但是 JS 的装饰器更多的是存在于对函数或者属性进行一些操作，比如修改他们的值，代理变量，自动绑定 this 等等功能。

所以，后文当中我就使用 TypeScript 来进行讲解，因为 TypeScript 已经完整的实现了装饰器。
虽然 Babel 也可以，但是需要各种配置，人懒，不想配置那么多。

但是却无法实现通过反射来获取究竟有哪些装饰器添加到这个类/方法上。

于是 Reflect Metadata 应运而生。

## 3.Reflect Metadata

Reflect Metadata 是 ES7 的一个提案，它主要用来在声明的时候添加和读取元数据。TypeScript 在 1.5+ 的版本已经支持它，你只需要：

```
npm i reflect-metadata --save。
在 tsconfig.json 里配置 emitDecoratorMetadata 选项。
```

Reflect Metadata 的 API 可以用于类或者类的属性上，如：

```js
function metadata(
  metadataKey: any,
  metadataValue: any
): {
  (target: Function): void;
  (target: Object, propertyKey: string | symbol): void;
};
```

### 3.1 语法

Declarative definition of metadata(元数据的声明性定义):

```js
class C {
  @Reflect.metadata(metadataKey, metadataValue)
  method() {
  }
}
```

Imperative definition of metadata(元数据的强制定义):

```js
Reflect.defineMetadata(metadataKey, metadataValue, C.prototype, "method");
```

Imperative introspection of metadata(元数据的强制性反思):

```js
let obj = new C();
let metadataValue = Reflect.getMetadata(metadataKey, obj, "method");
```

Reflect.metadata 当作 Decorator 使用，当修饰类时，在类上添加元数据，当修饰类属性时，在类原型的属性上添加元数据，如：

```js
@Reflect.metadata('inClass', 'A')
class Test {
  @Reflect.metadata('inMethod', 'B')
  public hello(): string {
    return 'hello world';
  }
}

console.log(Reflect.getMetadata('inClass', Test)); // 'A'
console.log(Reflect.getMetadata('inMethod', new Test(), 'hello')); // 'B' 
// 这里为什么要用 new Test()，用 Test 不行么？后文会讲到
```


它具有诸多使用场景。

## 4.获取类型信息

让我们声明下面的属性装饰器 :

```js
function logType(target : any, key : string) {
  var t = Reflect.getMetadata("design:type", target, key);
  console.log(`${key} type: ${t.name}`);
}
```

然后我们可以将它应用到类的一个属性上来获取它的类型 :

```js
class Demo{
  @logType // apply property decorator
  public attr1 : string;
}
```

类型元数据使用元数据键"design:type"

参数类型元数据使用元数据键"design:paramtypes"

返回值类型元数据使用元数据键"design:returntype"

## 5.自定义 metadataKey

除能获取类型信息外，常用于自定义 metadataKey，并在合适的时机获取它的值，示例如下：

```js
function classDecorator(): ClassDecorator {
  return target => {
    // 在类上定义元数据，key 为 `classMetaData`，value 为 `a`
    Reflect.defineMetadata('classMetaData', 'a', target);
  };
}

function methodDecorator(): MethodDecorator {
  return (target, key, descriptor) => {
    // 在类的原型属性 'someMethod' 上定义元数据，key 为 `methodMetaData`，value 为 `b`
    Reflect.defineMetadata('methodMetaData', 'b', target, key);
  };
}

@classDecorator()
class SomeClass {
  @methodDecorator()
  someMethod() {}
}

Reflect.getMetadata('classMetaData', SomeClass); // 'a'
Reflect.getMetadata('methodMetaData', new SomeClass(), 'someMethod'); // 'b'
```



## 6.类/属性/方法 装饰器

看这个例子

```js
@Reflect.metadata('name', 'geekjc')
class A {
  @Reflect.metadata('name', 'hello, geekjc')
  hello() {}
}

const objs = [A, new A, A.prototype]
const res = objs.map(obj => [
  Reflect.getMetadata('name', obj),
  Reflect.getMetadata('name', obj, 'hello'),
  Reflect.getOwnMetadata('name', obj),
  Reflect.getOwnMetadata('name', obj ,'hello')
])
// 大家猜测一下 res 的值会是多少？
```

res

```
[
  ['geekjc', undefined, 'geekjc', undefined],
  [undefined, 'hello, geekjc', undefined, undefined],
  [undefined, 'hello, geekjc', undefined, 'hello, geekjc'],
]
```

那么我来解释一下为什么回是这样的结果。
首先所有的对类的修饰，都是定义在类这个对象上面的，而所有的对类的属性或者方法的修饰，都是定义在类的原型上面的，并且以属性或者方法的 key 作为 property，这也就是为什么 getMetadata 会产生这样的效果了。
那么带 Own 的又是什么情况呢？
这就要从元数据的查找规则开始讲起了

## 7.原型链查找

类似于类的继承，查找元数据的方式也是通过原型链进行的。

就像是上面那个例子，我实例化了一个 new A()，但是我依旧可以找到他原型链上的元数据。

举个例子

```js
class A {
  @Reflect.metadata('name', 'hello')
  hello() {}
}

const t1 = new A()
const t2 = new A()
Reflect.defineMetadata('otherName', 'world', t2, 'hello')
Reflect.getMetadata('name', t1, 'hello') // 'hello'
Reflect.getMetadata('name', t2, 'hello') // 'hello'
Reflect.getMetadata('otherName', t2, 'hello') // 'world'

Reflect.getOwnMetadata('name', t2, 'hello') // undefined
Reflect.getOwnMetadata('otherName', t2, 'hello') // 'world'
```

## 8.例子

### 8.1 控制反转和依赖注入

在 Angular 2+ 的版本中，控制反转与依赖注入便是基于此实现，现在，我们来实现一个简单版：

```ts
type Constructor<T = any> = new (...args: any[]) => T;

const Injectable = (): ClassDecorator => target => {};

class OtherService {
  a = 1;
}

@Injectable()
class TestService {
  constructor(public readonly otherService: OtherService) {}

  testMethod() {
    console.log(this.otherService.a);
  }
}

const Factory = <T>(target: Constructor<T>): T => {
  // 获取所有注入的服务
  const providers = Reflect.getMetadata('design:paramtypes', target); // [OtherService]
  const args = providers.map((provider: Constructor) => new provider());
  return new target(...args);
};

Factory(TestService).testMethod(); // 1
```

### 8.2 Controller 与 Get 的实现

如果你在使用 TypeScript 开发 Node 应用，相信你对 Controller、Get、POST 这些 Decorator，并不陌生：

```
@Controller('/test')
class SomeClass {
  @Get('/a')
  someGetMethod() {
    return 'hello world';
  }

  @Post('/b')
  somePostMethod() {}
}
```

这些 Decorator 也是基于 Reflect Metadata 实现，这次，我们将 metadataKey 定义在 descriptor 的 value 上：

```js
const METHOD_METADATA = 'method'；
const PATH_METADATA = 'path'；

const Controller = (path: string): ClassDecorator => {
  return target => {
    Reflect.defineMetadata(PATH_METADATA, path, target);
  }
}

const createMappingDecorator = (method: string) => (path: string): MethodDecorator => {
  return (target, key, descriptor) => {
    Reflect.defineMetadata(PATH_METADATA, path, descriptor.value);
    Reflect.defineMetadata(METHOD_METADATA, method, descriptor.value); 
    // 关于为什么参数是target,key,descriptor，可以看我之前写的一篇文章[装饰器(Decorator)在React中的应用](https://www.geekjc.com/post/5a630df9f6a6db2832a57368)
  }
}

const Get = createMappingDecorator('GET');
const Post = createMappingDecorator('POST');
```

接着，创建一个函数，映射出 route：

```js
function mapRoute(instance: Object) {
  const prototype = Object.getPrototypeOf(instance);

  // 筛选出类的 methodName
  const methodsNames = Object.getOwnPropertyNames(prototype)
                              .filter(item => !isConstructor(item) && isFunction(prototype[item]))；
  return methodsNames.map(methodName => {
    const fn = prototype[methodName];

    // 取出定义的 metadata
    const route = Reflect.getMetadata(PATH_METADATA, fn);
    const method = Reflect.getMetadata(METHOD_METADATA, fn)；
    return {
      route,
      method,
      fn,
      methodName
    }
  })
};
```

因此，我们可以得到一些有用的信息：

 * ```js
    Reflect.getMetadata(PATH_METADATA, SomeClass); // '/test'
    
    mapRoute(new SomeClass());
    
    /**
     * [{
     *    route: '/a',
     *    method: 'GET',
     *    fn: someGetMethod() { ... },
     *    methodName: 'someGetMethod'
     *  },{
     *    route: '/b',
     *    method: 'POST',
     *    fn: somePostMethod() { ... },
     *    methodName: 'somePostMethod'
     * }]
     *
     */
    ```

 最后，只需把 route 相关信息绑在 express 或者 koa 上就 ok 了。

## 9.最后

其实所有的用途都是一个目的，给对象添加额外的信息，但是不影响对象的结构。这一点很重要，当你给对象添加了一个原信息的时候，对象是不会有任何的变化的，不会多 property，也不会有的 property 被修改了。
但是可以衍生出很多其他的用途。

Anuglar 中对特殊字段进行修饰 (Input)，从而提升代码的可读性。
可以让装饰器拥有真正装饰对象而不改变对象的能力。让对象拥有更多语义上的功能。

著作权归作者所有。
商业转载请联系作者获得授权，非商业转载请注明出处。
作者：ll
链接：https://www.geekjc.com/post/5c34498f1ee6dd2e881140df
来源：极客教程
[TOC]



# 设计模式在 TypeScript 中的应用 - 单例模式



## 定义

只有一个实例，并提供全局访问。

## 实现

思路：用一个变量来标识当前是否已经为某个类创建过对象，如果是，则在下一次获取该类的实例时，直接返回之前创建的对象，否则返回新对象。

### 饿汉模式

特点：类加载时就初始化。

```ts
class Singleton {

  private static instance = new Singleton()

  // 将 constructor 设为私有属性，防止 new 调用
  private constructor () {}

  static getInstance (): Singleton {
    return Singleton.instance
  }
}

const singleton1 = Singleton.getInstance()
const singleton2 = Singleton.getInstance()
console.log(singleton1 === singleton2) // true
```

对大部分使用者来说， 可以用模块来替代。

```ts
// someFile.ts
// ... any one time initialization goes here ...
export function someMethod() {}

// Usage
import { someMethod } from './someFile';
```

### 懒汉模式

特点：需要时才创建对象实例。

```ts
class Singleton {
  private static instance: Singleton
  
  private constructor () {}

  static getInstance (): Singleton {
    if (!Singleton.instance) {
      Singleton.instance = new Singleton()
    }
    return this.instance
  }
}

const singleton1 = Singleton.getInstance()
const singleton2 = Singleton.getInstance()
console.log(singleton1 === singleton2) // true
```

## 简单栗子

```ts
class Singleton {
  private constructor (name: string, age: number) {
    this.name = name
    this.age = age
  }

  private static instance: Singleton

  public name: string
  public age: number

  static getInstance (
      name: string,
      age: number
    ): Singleton {
    if (!this.instance) {
      this.instance = new Singleton(name, age)
    }
    return this.instance
  }
}

const singleton1 = Singleton.getInstance('Mary', 20)
const singleton2 = Singleton.getInstance('Jack', 20)
console.log(singleton1, singleton2)
```







<https://segmentfault.com/a/1190000012551665>



<https://jkchao.github.io/typescript-book-chinese/tips/singletonPatern.html>
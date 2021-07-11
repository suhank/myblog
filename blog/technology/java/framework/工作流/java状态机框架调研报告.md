# java状态机框架调研报告

2020年05月28日 

## 什么是状态机

状态机是有限状态自动机的简称，是现实事物运行规则抽象而成的一个数学模型。

> FSM[有限状态机]是一种抽象的机器，在任何给定的时间里，它可以精确地处于有限状态中的一个状态 ——-维基百科

从形式上讲，有限状态机是一个五元机(𝜮、𝑺、𝑺o、𝜹、𝑭)，其中。

- 𝜮是一个输入动作字母表。
- 𝑺是一组可能的状态。
- 𝑺o是初始状态。
- 𝜹是一个状态转换函数𝜹 :𝑺 x 𝜮 → 𝑺（从字母表输入后，从一个状态过渡到另一个状态）。
- 𝑭是一组结束状态。

举个例子，图书馆借书，流程图如下：

![image](java状态机框架调研报告.assets/1)



匹配状态图，我们有了

- 定义了可能的输入的动作（𝜮--借出、归还、损坏、丢失、修复和查找）
- 有限数量的状态（𝑺--库存、借出、损坏和丢失）
- 初始状态（𝑺o--库存）。 通过对可能的状态和过渡的明确定义，现在在数学上不可能同时处于两个状态。

##### 将上述状态翻译成代码

```
{
  "initial": "In Stock",   #初始状态
  "states": {
    "In Stock": {         #库存状态只有 “借出” 动作
      on: {
        "Lend": "Lent",
      },
    },
    "Lent": {             #借出状态有 “return”，“damage”，“lose”三个动作，并且对应过渡之后的状态
      on: {
        "Return": "In Stock",
        "Damage": "Damaged",
        "Lose": "Lost",
      },
    },
    "Damaged": {
      on: {
        "Repair": "In Stock",
      },
    },
    "Lost": {
      on: {
        "Find": "In Stock",
      },
    },
  },
}

复制代码
```

状态机有四个核心概念，这是所有状态机的基础

- State ，状态。一个状态机至少要包含两个状态。
- Event ，事件。事件就是执行某个操作的触发条件或者口令。“借书”就是一个事件。
- Action ，动作。事件发生以后要执行动作。例如事件是“借书”，动作是“借”。编程的时候，一个 Action 一般就对应一个函数。 动作是在给定时刻要进行的活动的描述。有多种类型的动作：
  - 进入动作（entry action）：在进入状态时进行
  - 退出动作：在退出状态时进行
  - 输入动作：依赖于当前状态和输入条件进行
  - 转移动作：在进行特定转移时进行
- Transition ，过渡。也就是从一个状态变化为另一个状态。

对状态机输入一个事件，状态机会根据当前状态和触发的事件唯一确定一个状态迁移。

## 为什么我们要用状态机

- 业务中涉及到一些关于状态的操作，常见的就是订单，每个订单都会有自己的状态，订单的一些行为受限于当前订单的状态，订单的状态直接用常量表示，业务进行前的检查部分通过if判断来检测当前单据是否可以流转到目标状态，逻辑里充满大量判断，违背了设计原则
- 业务发展的比较快，某些订单状态不停的增加，每一次增加都需要改动业务中使用到状态的相关代码，更糟的的是这些代码可能遍布于多个类的多个方法中，不仅增加发布的风险也同时增加了测试难度。
- 状态及状态转换与业务解耦

总结：代码高耦合、低内聚、不易扩展，可维护性差，可测试性差，代码不易理解，用状态机重构

## 优势

通过应用状态机的方式来优化业务系统，将所有的状态、事件、动作都抽离出来，对复杂的状态迁移逻辑进行统一管理，来取代冗长的 if else 判断，使系统中的复杂问题得以解耦，变得直观、方便操作，使系统更加易于维护和管理。

用状态机来管理对象生命流的好处更多体现在代码的可维护性、可测试性上，明确的状态条件、原子的响应动作、事件驱动迁移目标状态，对于流程复杂易变的业务场景能大大减轻维护和测试的难度。

## 状态机技术选型的考量

1. 上手难度和速度，文档是否齐全
2. 改动的代码量
3. 状态机所能提供的功能：仅仅是提供状态判断？持久化？异步？
4. 用户人数是否多，是否有真实项目应用。有没有前人踩过坑，可靠性比较高。
5. 社区活跃度

## 选取了几款Gihub排名靠前的Java开源状态机对比——2020.5.27

| 开源状态机                                                   | 优点                                                         | 缺点                                                         | 改动                                                         | 活跃度                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [squirrel-foundation](https://github.com/hekailiang/squirrel) | 1、代码清晰结构良好，轻量级，文档和测试用例真的非常的很详细，扩展和维护比较容易 2、最新的0.3.9.11版本功能很全，支持基本的动作exit、transition、entry，并且围绕这些的动作实现很多扩展，粒度很细。 3、支持声明式和编程式编程。 4、支持异步和延时事件，可自定义线程池。 5、支持事件监听器。 6、支持分层状态、子状态 7、StateMachine实例创建开销小，单例复用的生命流管理更清晰，避免复用产生死锁 8、支持读取历史配置，支持上下文传递 | 1、这点见仁见智，代码太过约定大于配置,比如`transitFrom[SourceStateName]To[TargetStateName]On[EventName]`和callMethod方法传的方法名字符串和注解定义状态转换@Transit(from="A", to="B", on="GoToB") ,代码本身可能比较难受 | 不影响原来代码，只需按照业务流程确定好业务边界和流程流转，再创建状态机即可 | star数1.4k,  最近的有效更新在2018年作者未来有计划更新新功能，使用的人挺多 |
| [stateless4j](https://github.com/stateless4j/stateless4j)    | 1、十分轻量级的实现，比squireel还要精简，代码量很少 2、支持基本的事件迁移、exit/entry action、guard、dynamic permit(相同的事件不同的condition可到达不同的目标状态) 3、足够轻量所以接入方便，上手快二次开发难度低 | 1、支持的动作有entry exit action，但transition action略复杂 2、本身支持的action少，导致扩展很有限 3、不支持持久化和上下文传参,随用随new不过开销很小 | 轻量级的实现，引入现有项目上手非常快也很简洁易懂             | star数556，2.6.0版本在2019.9月发布。但是活跃度是挺不高的     |
| [statefulj](https://github.com/statefulj/statefulj)(个人相当不推荐) | emmmmmm                                                      | 从statefulj的文档上得知，它分成了三部分，一个是statefulj FSM状态机的基本实现，一个是statefulj persistence支持FSM的持久化，一个是集成两者的statefulj framework框架。 1、statefulj FSM使用麻烦，可读性差，并且初始化状态还要用创建persistence？，支持动作少，只支持transition action 2、statufulj framew在继承上述缺点之后，想成为一个web框架，这对代码侵入性极大极大，而且自己集成spring依赖太多，太重并且实现不友好 (这个名字有点点像stateless4j并且我看demo发现一点点squirrel用法的影子) | 改动太大                                                     | star数111。                                                  |
| [easy-states](https://github.com/j-easy/easy-states)         | 1、这个比stateless4j还轻量 2、真的很轻，很轻，非常轻 3、因为真的很轻没什么开发难度，我觉得这可以算用来当代码重构的工具包了 | 1、因为比stateless4j还轻，功能也比他少，跟statelessful相反，只支持transition action，其他不支持，精简版的stateless4j | 改动很小，等于外部包装一层                                   | star数100，2020.03.13发布2.0.0版本。                         |
| [JState](https://github.com/UnquietCode/JState)              | 1、JState = 精简版squirrel+stateless4结合两者的优点，功能齐全又很轻量 2、支持基本的entry exit transition action,还支持循环 3、可异步 | 1、不支持持久化和上下文，随用随new但开销小。 值得注意的就是它的异步是newSingleThreadExecutor实现的，但是线程执行异常时它捕获到后的操作是shutdown和cancel，最后又重新初始化了一个newSingleThreadExecutor，不懂为什么要这样 | 跟stateless4差不多，改动方便                                 | star数80，最后更新是在2018.11月                              |
| [spring-statemachine](https://github.com/spring-projects/spring-statemachine) | 加强版squirrel，功能很全                                     | 1、spring的状态机是个单例bean会交给spring管理，也就是说同一类的所有业务都共用一个状态机，这意味着状态机的状态是被共享的(提供了builder构造方法随用随new实现存在多个状态机但是不能解决共享状态)。 2、在当前状态执行不正确的事件时，spring state machine是捕获了异常，返回一个false，如果你想要针对异常做处理你得手动编码 3、由于状态机是单例，举个例子有两个待付款的订单A和B，A调用了付款状态机完成操作后，该状态机的状态就变成结束，此时B再去调用反而没有用了，因为这个付款状态机是被共享的，此时状态已经改成结束，自然B调用失败，这一点真的很严重，当然将builder设置为多例bean，这样就没问题了 | 改动量一般                                                   | star数888，现在稳定版2.2.0，3.0版本开发中，spring官方项目    |


 以下是简单的代码片段可以先大概了解各个开源状态机的用法，详细的代码请去各自的github上了解。

## 图书馆租书

### squirrel-foundation

```java
    // 2. Define State Machine Class
    @StateMachineParameters(stateType=BookState.class, eventType=BookEvent.class, contextType=Integer.class)
    static class StateMachineSample extends AbstractUntypedStateMachine {
        protected void fromStockToLent(BookState from, BookState to, BookEvent event, Integer context) {
            System.out.println("Transition from '"+from+"' to '"+to+"' on event '"+event+
                    "' with context '"+context+"'.");
        }

        protected void ontoB(String from, String to, BookEvent event, Integer context) {
            System.out.println("Entry State \'"+to+"\'."+event.name());
        }
    }

    public static void main(String[] args) {
        // 3. Build State Transitions
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSample.class);
        builder.externalTransition().from(BookState.STOCK).to(BookState.LENT).on(BookEvent.LEND).callMethod("fromStockToLent");
        builder.onEntry(BookState.LENT).callMethod("ontoB");


        // 4. Use State Machine
        UntypedStateMachine fsm = builder.newStateMachine(BookState.STOCK);
        fsm.fire(BookEvent.LEND);

        System.out.println("Current state is "+fsm.getCurrentState());
    }
```

### stateless4j

```java
        StateMachineConfig<BookState, BookEvent> config = new StateMachineConfig<>();

        config.configure(BookState.STOCK)
                .permit(BookEvent.LEND, BookState.LENT);

        StateMachine<BookState, BookEvent> machine = new StateMachine<>(BookState.STOCK, config);
 
        machine.fire(BookEvent.LENT);
      
```

### statefulj(真的别用这个)

```
        String lendEvent = BookEvent.LEND.name();

        State<Book> stockState = new StateImpl<Book>(BookState.STOCK.name());
        State<Book> lentState = new StateImpl<Book>(BookState.LENT.name());

        Action<Book> lentAction = new BookAction("借书");

        stockState.addTransition(lendEvent, lentState, lentAction);

        List<State<Book>> states = new LinkedList<State<Book>>();
        states.add(stockState);
        states.add(lentState);
        MemoryPersisterImpl<Book> persister = new MemoryPersisterImpl<Book>(states,stockState);
        FSM<Book> fsm = new FSM<Book>("Book FSM",persister);
        Book book = new Book();
        book.setState(BookState.STOCK.name());
        try {
            fsm.onEvent(book, lendEvent);
        } catch (TooBusyException e) {
            e.printStackTrace();
        }

    }

    public static class BookAction<T> implements Action<T> {
        String what;
        public BookAction(String what) {
            this.what = what;
        }
        @Override
        public void execute(T stateful, String event, Object ... args) {
            System.out.println("Hello " + what);
        }
    }
复制代码
```

### easy-states

```
        State stock = new State(BookState.STOCK.name());
        State lent = new State(BookState.LENT.name());
        State lost = new State(BookState.LOST.name());

        Set<State> states = new HashSet<>();
        states.add(stock);
        states.add(lent);
        states.add(lost);
        
        Transition lentTransition = new TransitionBuilder()
                .name("lent")
                .sourceState(stock)
                .eventType(LentEvent.class)
                .eventHandler(new Lend())
                .targetState(lent)
                .build();


        Transition returnTransiton = new TransitionBuilder()
                .name("return")
                .sourceState(lent)
                .eventType(ReturnEvent.class)
                .eventHandler(new ReturnBooke())
                .targetState(stock)
                .build();

        FiniteStateMachine fsm = new FiniteStateMachineBuilder(states, stock)
                .registerTransition(lentTransition)
                .registerTransition(returnTransiton)
                .registerFinalState(lost)
                .build();

        fsm.fire(new LentEvent());
        fsm.fire(new ReturnEvent());
    }

    static class LentEvent extends AbstractEvent { }

    static class ReturnEvent extends AbstractEvent { }


    static class Lend implements EventHandler<LentEvent>{

        @Override
        public void handleEvent(LentEvent event) throws Exception {
            System.out.println("借出去了");
        }
    }

    static class ReturnBooke implements EventHandler<ReturnEvent>{

        @Override
        public void handleEvent(ReturnEvent event) throws Exception {
            System.out.println("还回去了");
        }
    }
复制代码
```

### JState

```
        EnumStateMachine<BookState> esm = new EnumStateMachine<>(BookState.STOCK);
        
        TransitionHandler<BookState> cb = (from, to) -> System.out.println("调用Transition，"+from.toString()+to.toString());
        
        esm.addTransitions(cb, BookState.STOCK, BookState.LENT);
        
        esm.onEntering(BookState.LENT,  (state)->System.out.println("进入："+state));
        
        esm.transition(BookState.LENT);

复制代码
```

### spring-statemachine

```
@WithStateMachine(name = "bookStateMachine")
public class BookSingleEventConfig {

    /**
     * 当前状态
     */
    @OnTransition(target = "STOCK")
    public void init() {
        System.out.println("---现在的状态是在库存---");
    }

    /**
     * UNPAID->WAITING_FOR_RECEIVE 执行的动作
     */
    @OnTransition(source = "STOCK", target = "LENT")
    public void lend() {
        System.out.println("---把书借出去了---");
    }


    @OnStateEntry(target = "LENT")
    public void initLent() {
        System.out.println("---现在的状态是已出借---");
    }


    /**
     * WAITING_FOR_RECEIVE->DONE 执行的动作
     */
    @OnTransition(source = "LENT", target = "DAMAGED")
    public void damage() {
        System.out.println("---借出去的书损坏---");
    }

}

@Configuration
@EnableStateMachine(name = "bookStateMachine")
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<BookState, BookEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(StateMachineStateConfigurer<BookState, BookEvent> states) throws Exception {
        states.withStates().initial(BookState.STOCK).states(EnumSet.allOf(BookState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BookState, BookEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(BookState.STOCK)
                    .target(BookState.LENT)
                    .event(BookEvent.LEND)
                    .action(action())
                .and()
                .withExternal()
                    .source(BookState.LENT)
                    .target(BookState.DAMAGED)
                    .event(BookEvent.DAMAGE);
    }


    @Bean
    public Action<BookState, BookEvent> action() {
        return new Action<BookState, BookEvent>() {

            @Override
            public void execute(StateContext<BookState, BookEvent> context) {
                System.out.println("这里是action："+context);
            }
        };
    }
}


@SpringBootTest
class StateMachineDemoApplicationTests {

    @Autowired
    private StateMachine bookSingleMachine;

    @Test
    void contextLoads() {
        // 创建流程
        bookSingleMachine.start();

        // 触发PAY事件
        bookSingleMachine.sendEvent(BookEvent.LEND);

        // 获取最终状态
        System.out.println("最终状态：" + bookSingleMachine.getState().getId());
    }

}

复制代码
```



## 个人建议

| 开源状态机                                                   | 个人建议                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [squirrel-foundation](https://github.com/hekailiang/squirrel) | 功能很多所以略微复杂但并不难，若业务复杂并考虑扩展性可用这个 |
| [stateless4j](https://github.com/stateless4j/stateless4j)    | 业务比较简单不想改动太多可以使用这个                         |
| [statefulj](https://github.com/statefulj/statefulj)(个人相当不推荐) | 了解就好，十分不推荐，试试demo便知。                         |
| [easy-states](https://github.com/j-easy/easy-states)         | 只是为了重构代码方便管理状态变化可用这个                     |
| [JState](https://github.com/UnquietCode/JState)              | 这个项目结合两家之长，如果不想要squirrel那么繁琐但是又担心stateless4j功能太少，不妨选择这个 |
| [spring-statemachine](https://github.com/spring-projects/spring-statemachine) | 除了重，学习成本高，就是上述的状态机共享状态的问题，一类状态机被许多状态使用并且还是共享状态，这是很不合理的，我只能把它设置为多例，这样就不会共享 |

## 总结

#### 功能对比

> **spring-statemachine > squirrel > stateless4j > JState > easy-states**



#### 上手难度和代码改动量

> **easy-states ≈ stateless4j < JState < squirrel < spring-statemachine**



## 个人调研报告，带有强烈主观意见，请注意

参考文档

> [blog.smartive.ch/what-state-…](https://blog.smartive.ch/what-state-machines-are-and-why-we-use-them-5ea55183be09)

> [segmentfault.com/a/119000000…](https://segmentfault.com/a/1190000009906317)

> [childe.net.cn/2018/04/28/…](http://childe.net.cn/2018/04/28/状态机选型简记/)


作者：珅珅君
链接：https://juejin.cn/post/6844904170852450318
来源：掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。


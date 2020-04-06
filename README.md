# perf-unit
## 项目概述
项目的设计思想和结构主要参考杭州有赞公司的这篇文章 [有赞单元测试实践](https://tech.youzan.com/youzan-test-practice/)，基于junit4进行二次开发。
### 数据库层
![有赞单云测试实践](/Users/huayongqi694/coding/perf-unit/README.assets/有赞单云测试实践.png)
如上图，perf-unit框架的将单元测试数据库统一成springtest+dbunit+h2的结构，数据源文件支持xml/csv/excel三种格式，通过@SecTest注解引入。perf-unit的主要创新点是引入了动态数据源，让有依赖数据库层的测试用例也支持并发执行。
```java
 @Test
 @SecTest(enablePrepare=true,
   prepareDateType = PrepareDataType.XML2DB,
   prepareDateConfig = {"sampleData.xml"},
   enableCheck = true,
   checkConfigFiles = {"CheckConfigFile.json"})
 public void testXml() throws Exception {
  List<Person> personList = this.personService.find("hil");
  Assert.assertEquals(1, personList.size());
 }
```
### 并发设计
基于spring的项目在批量执行多个测试用例场景时，依赖springtest框架进行测试，单线程测试用例跑的比较慢。
junit框架默认未提供并发注解支持，junit有很多第三方扩展插件支持并发测试，其中idea的plugin商店并发扩展插件[junit4-parallel-runner](https://plugins.jetbrains.com/plugin/12959-junit4-parallel-runner)。testng框架原生支持并发，详见[testng并发](https://howtodoinjava.com/testng/testng-executing-parallel-tests/)。
perf-unit也提供了SecParallelSpringRunner，SecParallelSuite，支持最小到method级别的并发。
### 测试用例
测试用例见perf-unit-test项目，目前基于springboot2.x测试通过，已提供完整的jpa和mybatis测试用例。
在使用perf-unit过程中，需要做的额外工作将我们日常的mysql数据库的建表sql转换成h2支持的格式，然后通过dbunit提供的工具类连mysql导出数据到对应的xml/excel/csv文件即可使用。

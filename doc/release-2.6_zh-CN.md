Clobaframe v2.6 Release Notes (zh-CN)
===

适用于从桌面和移动应用程序到集群程序的弹性框架

新版本特性
---

Clobaframe 2.6 主要增加了 MVC 模块及 Template Engine 模块。跟绝大部分 Web 框架一样，前者负责解析 Web Request 路由以及派送到模版引擎，组装响应的内容以及处理异常再返回给请求者；后者负责解析页面脚本然后生成相应的 HTML 代码（Clobaframe 的模版引擎的脚本解析由 Apache Velocity 完成）。此次添加的两个模块连同上一次更新增加的 IoC 模块，组成了完整的应用程序基础框架。

作用及目标
---

Clobaframe 基础框架的主要目的是用于构建 Desktop 和 Mobile APP，因为在实际应用中发现使用 Spring Framework 框架的应用程序（比如使用 Web UI 作为交互的应用程序）在启动时大约有 1 到 2 秒的启动时间，这个预热时间对于 Web Application 可能没任何影响，不过对于桌面或者移动应用程序来说这个启动过程的等待可能是难以忍受的，在这个背景下于是设计了 Clobaframe 基础框架，这个基础框架几乎没有启动预热时间。

代码的迁移
---

Clobaframe 基础框架沿用 Spring Framework 的基础接口，也就是说它可以跟 Spring 框架混合使用，比如使用 Spring IoC + Spring MVC + Clobaframe Template Engine 这样的组合是完全可以的，实际上三个模块都可以随意更换为 Clobaframe 的，并且无需更改代码。这个特性对于从 Spring Framework 迁移到 Clobaframe 非常方便，因为它不需要开发者重新学习一套框架，只需使用自己已掌握已熟悉的方法编写应用程序，然后稍微修改一下配置文件就可以把项目轻松迁移到 Clobaframe。

关于整个组件库
---

整个 Clobafame 组件库由 4 个项目共 32 个模块组成。4 个项目分别为基础框架、Web 框架、小型集群库、云平台库。其中后两者用于抽象及封装各种必要的底层服务，让基于 Clobaframe 的程序有较强的弹性。

比如要开发一个个人 wiki 应用程序，从移动APP，到个人Web程序，到服务于多人的Web服务，直至到服务于超大量用户的服务，都可以沿用相同的框架相同的设计方式，其中对于后两种的升级，只需把 Clobaframe 基础框架更换为 Cluster，或者把 Cluster 更换为 Cloud Platform 的相应组件，然后对应用程序的作少量的修改即可。Clobaframe 框架的原始目的正是为了降低升级应用程序的规模时的难度以及减少升级时所带来麻烦。

源代码
---

基础框架 https://github.com/ivarptr/clobaframe
Web 框架 https://github.com/ivarptr/clobaframe-web
小集群 （尚在开发中）
Cloud 平台 https://github.com/ivarptr/clobaframe-cloud

# 简易Logdb项目

## logdb 1.3

> 旧版本在 resource/template 中，说明在README_XX.md中

> 更新项：  
引入线程概念、主线程main作为udp接收者，新增两个线程解析和保存数据
不采用批量保存，一条一条的保存数据，保证接口调用的顺序在数据库里面正确的体现

### UDP客户端
---
> php的例子 （其他根据参数大概改一改）  
$request: 请求的信息    
$response: 返回的信息

### UDP客户端参数
```php
'module' => 'api',
'uid' => $uid ? $uid : 0,
'apiName' => $path,
'requestMethod' => $request->method(),
'paramJson' => substr($param, 0, 1000),
'response' => '',
'status' => 200,
'errCode' => 0,
'errMessage' => '',
'userIp' => '',
'serverIp' => '',
'endTime' => 0,
'spendTime' => 0,
'system' => $system ? $system : '',
'brand' => $brand ? $brand : '',
'model' => $model ? $model : '',
'version' => $version ? $version : '',
'appVersion' => $appVersion ? $appVersion : '',

...

$sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
socket_sendto($sock, $message, $len, 0, 'ip地址', port端口号);
socket_close($sock);
```

### 服务端
> 把该项目打包，执行下面命令

```shell
java -jar logdb-1.3-SNAPSHOT.jar >> /data/logs/java_console/api_log_log/console.log 2>&1 &
```

> 强制结束命令，使用下面命令，会回调处理未保存的数据

```shell
kill -15 pid
```

### 数据库
> 根据不同的模块(module)把数据插入不同的表中 api_log_yyyMMdd admin_log_yyyMM 每月一张新表，支持自动创建数据表

```sql
CREATE TABLE `admin_log_201812` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `api_name` varchar(100) NOT NULL DEFAULT '' COMMENT '接口uri',
  `request_method` varchar(10) NOT NULL DEFAULT '' COMMENT '接口访问方式',
  `param_json` varchar(1024) NOT NULL DEFAULT '' COMMENT '参数信息',
  `response` varchar(4096) NOT NULL DEFAULT '' COMMENT '返回结果',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '返回状态码',
  `err_code` int(11) NOT NULL DEFAULT '0' COMMENT '返回的错误码',
  `err_message` varchar(255) NOT NULL DEFAULT '' COMMENT '返回的错误信息',
  `user_ip` varchar(32) NOT NULL DEFAULT '' COMMENT '用户IP',
  `server_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '服务IP',
  `end_time` int(11) NOT NULL DEFAULT '0' COMMENT '数据返回时间',
  `spend_time` int(11) NOT NULL DEFAULT '0' COMMENT '接口耗时',
  `system` varchar(20) NOT NULL DEFAULT '' COMMENT '操作系统',
  `brand` varchar(32) NOT NULL DEFAULT '' COMMENT '手机品牌',
  `model` varchar(64) NOT NULL DEFAULT '' COMMENT '手机型号',
  `version` varchar(20) NOT NULL DEFAULT '' COMMENT '微信程序版本',
  `app_version` varchar(20) NOT NULL DEFAULT '' COMMENT '应用程序版本',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_api_log_t_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口信息调用表'

```

## 联系QQ
> 1109563194

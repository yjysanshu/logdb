# 简易Logdb项目

### UDP客户端
---
> php的例子 （其他根据参数大概改一改）  
$request: 请求的信息    
$response: 返回的信息

```php
$businessData = $request->businessData;
$param = json_encode($businessData);
$data = [
    'uid' => $request->header('X_UUID') ?? 0,
    'apiName' => $request->path(),
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
    'version' => $request->header('X_VERSION') ?? '',
];

$time = TimeHelper::millisecond();

$endTime = TimeHelper::millisecond();
$data['response'] = '';
$data['status'] = $response->getStatusCode();
$data['errCode'] = $response->original['errcode'];
$data['errMessage'] = $response->original['msg'];
$data['userIp'] = getIp();
$data['serverIp'] = $_SERVER["SERVER_ADDR"] ?? '';
$data['endTime'] = intval($endTime / 1000);
$data['spendTime'] = $endTime - $time;

$message = json_encode($data);
$len = strlen($message);

$sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
socket_sendto($sock, $message, $len, 0, 'ip地址', port端口号);
socket_close($sock);
```

### 服务端
> 把该项目打包，执行下面命令

```shell
java -jar logdb-1.0-SNAPSHOT.jar >> /data/logs/java_console/api_log_log/console.log 2>&1 &
```

> 强制结束命令，使用下面命令，会回调处理未保存的数据

```shell
kill -15 pid
```

### 数据库
> 数据表表名 api_log_yyyMMdd，每天一张新表，现在没有支持自动加

```sql
CREATE TABLE `api_log_20181130` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `api_name` varchar(100) NOT NULL DEFAULT '' COMMENT '接口uri',
  `request_method` varchar(10) NOT NULL DEFAULT '' COMMENT '接口访问方式',
  `param_json` varchar(1024) NOT NULL DEFAULT '' COMMENT '参数信息',
  `response` varchar(4096) NOT NULL DEFAULT '' COMMENT '返回结果',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '返回状态码',
  `err_code` int(11) NOT NULL DEFAULT '0' COMMENT '返回的错误码',
  `err_message` varchar(255) NOT NULL DEFAULT '' COMMENT '返回的错误信息',
  `user_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '用户IP',
  `server_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '服务IP',
  `end_time` int(11) NOT NULL DEFAULT '0' COMMENT '数据返回时间',
  `spend_time` int(11) NOT NULL DEFAULT '0' COMMENT '接口耗时',
  `version` varchar(20) NOT NULL DEFAULT '' COMMENT '应用程序版本',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_api_log_t_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口信息调用表'

```
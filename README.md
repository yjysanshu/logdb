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

```shell
java -jar logdb-1.0-SNAPSHOT.jar >> /data/logs/java_console/api_log_log/console.log 2>&1 &
```
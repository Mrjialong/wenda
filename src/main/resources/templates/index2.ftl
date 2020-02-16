<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8"/>
    <title></title>
</head>
<body>
<h1>${vos}</h1>
FreeMarker模板引擎
<#list vos as vo>
  用户名：${vo}
</#list>
</body>
</html>
# Interface Description

* HTTPs GET
* Need signature with common params
* http host: api.adfly.vn

# Signature algorithm

## how to get your appKey and appSecret

We have assigned appKey and appSecret to each publisher. The method of obtaining it is shown in the figure below

![](C:\diskpc\gitlib\adfly-ssp\doc\pub.png)

* Note: pubId and appKey and appSecret must match.

## There are 5 form common params in all HTTPs GET request:

| param | Description | type | range | required In http form|
|:--:|:--:|:--:|:--:|:--:|
| appKey | Assign by offline | string | - | true |
| deviceId | deviceId: Identify device， Andriod： adverserId, iOS: idfs, Other: none| string | - | true |
| nonce | A random string, 6 bytes| string | - | true |
| timestamp | System.currentTimeMillis() | long | - | true |
| signature | Request signature | string | - | true |

## The secret key used for calcing signature
| param | Description | type | range |
|:--:|:--:|:--:|:--:|
| appSecret | Assign by offline, as a parameter to calc signature <b>can not include in HTTP form params</b> | string | - |


## How to calc signature

* The parameters for calculating the signature include the following parameters
1. 5 common form params in all HTTPs GET request
2. The secret key used for calcing signature
3. Other form parameters enumerated in each API

* There are three step:
1. get appKey and appSecret from our dashboard(ask your BD for Account), each publisher has its own appKey and appSecret.
2. Combine all of the parameters to a <b>stringToCalcSignature</b>. The format follow: key=value[,key1=value1]. <b> Keys are sorted by dict order. </b>
3. use sha1.sum(<b>stringToCalcSignature</b>) calc signature

* check signature please use: https://api.adfly.vn/devtools/sign

## API
## Query daily report data for a specific widget

### Request
* Method: HTTPs GET
* URI: /api/report/widget
* HTTP GET | HTTPs GET
* Parameter Description

| param | Description | type | range | required |
|:--:|:--:|:--:|:--:|:--:|
| begDate | begin date(include), format as: %Y-%m-%d | string | - | true |
| endDate | end date(include), format as: %Y-%m-%d | string | - | true |
| country | country code, empty mean all country | string | vn th in id ... | false |
| platform   | platform code, empty mean all platform | string | ios android pc(means other) | false |
| pubId  | publisher id(ask for BD of adfly) | int | - | true |
| widgetId  | widget id (means placement id)(ask for BD of adfly) | int | - | true |


### response

* Json body
* Parameter

| param | type | range | Description|
|:--:|:--:|:--:|:--:|
| code | int | - | if 0: success; else failed |
| message | string | - | error message |
| data.date | string | - | date |
| data.impressions | int | - | Number of landing page impressions |
| data.ecpm | float | - | ECPM |
| data.revenue | float | - | Your income (in U.S. dollars) |


### request example
* appKey := hayko.tv
* Secret := f861789273afca09efda8cfad61b50cf

```
curl "http://127.0.0.1:15000/api/report/widget?appKey=hayko.tv&begDate=2021-04-23&country=&deviceId=&endDate=2021-04-29&nonce=XVlBzg&pid=595&platform=&signature=2d0910e8ed4a42974228e40cc91900c7407df9c9&timestamp=1619702071583&widgetId=818"
```

### response example
```
{
  "code": 0,
  "message": "",
  "data": [
    {
      "date": "2021-04-23",
      "impressions": 188209,
      "ecpm": 0.0917,
      "revenue": 17.2642
    },
    {
      "date": "2021-04-24",
      "impressions": 408999,
      "ecpm": 0.1076,
      "revenue": 44.0067
    }
  ]
}

```

## Query all widgets report data in a specific time interval


### Request
* Method: HTTPs GET
* URI: /api/report/widgets
* HTTP GET | HTTPs GET
* Parameter Description

| param | Description | type | range | required |
|:--:|:--:|:--:|:--:|:--:|
| begDate | begin date(include), format as: %Y-%m-%d | string | - | true |
| endDate | end date(include), format as: %Y-%m-%d | string | - | true |
| country | country code, empty mean all country | string | vn th in id ... | false |
| platform   | platform code, empty mean all platform | string | ios android pc(means other) | false |
| pubId  | publisher id(ask for BD of adfly) | int | - | true |


### response

* Json body
* Parameter

| param | type | range | Description|
|:--:|:--:|:--:|:--:|
| code | int | - | if 0: success; else failed |
| message | string | - | error message |
| data.widgetId | string | - | widget id(placement id) |
| data.style | string | - | widget style |
| data.widgetName | string | - | widget name |
| data.impressions | int | - | Number of landing page impressions |
| data.ecpm | float | - | ECPM |
| data.revenue | float | - | Your income (in U.S. dollars) |

### request example
* appKey := hayko.tv
* Secret := f861789273afca09efda8cfad61b50cf

```
curl "http://127.0.0.1:15000/api/report/widgets?appKey=hayko.tv&begDate=2021-04-23&country=&deviceId=&endDate=2021-04-29&nonce=XVlBzg&platform=&pubId=595&signature=ddf95d41d409a7d1eea8d64f4e4dc45b57258aff&timestamp=1619701107574"
```

### response example
```
{
  "code": 0,
  "message": "",
  "data": [
    {
      "widgetId": 818,
      "style": "floatIcon",
      "widgetName": "Float icon",
      "impressions": 2430017,
      "ecpm": 0.0789,
      "revenue": 191.6749
    }
  ]
}

```

# appendixes

## country code

| code | country |
|:--:|:--:|
| vn | Vietnam |
| th | Thailand |
| id | Indonesia |
| ph | the Philippines |
| my | Malaysia |
| in | India |
| bd | Bangladesh |
| br | Brazil |
| mx | Mexico |

## platform code
ios | android


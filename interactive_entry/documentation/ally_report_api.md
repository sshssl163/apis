# Interface Description

- HTTPs GET
- Need signature with common params
- http host: api.adfly.vn

# Signature algorithm

## how to get your allyid and appSecret

We have assigned allyid and appSecret to each dsp. The method of obtaining it is shown in the figure below

![](C:\diskpc\gitlib\adfly-ssp\doc\pub.png)

- Note: allyid and appSecret must match.

## There are 1 form common params in all HTTPs GET request:

| param  |    Description    |  type  | range | required In http form |
| :----: | :---------------: | :----: | :---: | :-------------------: |
| allyid | Assign by offline | string |   -   |         true          |

## The secret key used for calcing signature

|   param   |                                          Description                                           |  type  | range |
| :-------: | :--------------------------------------------------------------------------------------------: | :----: | :---: |
| appSecret | Assign by offline, as a parameter to calc signature <b>can not include in HTTP form params</b> | string |   -   |

## How to calc signature

- The parameters for calculating the signature include the following parameters

1. 5 common form params in all HTTPs GET request
2. The secret key used for calcing signature
3. Other form parameters enumerated in each API

- There are three step:

1. get appKey and appSecret from our dashboard(ask your BD for Account), each publisher has its own appKey and appSecret.
2. Combine all of the parameters to a <b>stringToCalcSignature</b>. The format follow: key=value[,key1=value1]. <b> Keys are sorted by dict order. </b>
3. use sha1.sum(<b>stringToCalcSignature</b>) calc signature

## API

## Query daily report data for a specific ally

### Request

- Method: HTTPs GET
- URI: /api/report/widget
- HTTP GET | HTTPs GET
- Parameter Description

|  param  |               Description                |  type  | range | required |
| :-----: | :--------------------------------------: | :----: | :---: | :------: |
| begDate | begin date(include), format as: %Y-%m-%d | string |   -   |   true   |
| endDate |  end date(include), format as: %Y-%m-%d  | string |   -   |   true   |

### response

- Json body
- Parameter

|      param       |  type  | range |            Description             |
| :--------------: | :----: | :---: | :--------------------------------: |
|       code       |  int   |   -   |     if 0: success; else failed     |
|     message      | string |   -   |           error message            |
|    data.date     | string |   -   |                date                |
| data.impressions |  int   |   -   | Number of landing page impressions |
|   data.revenue   | float  |   -   |   Your income (in U.S. dollars)    |

## algorithm code

```golang
type Pair struct {
	First  string
	Second string
}
type Params []Pair

func (a Params) Len() int           { return len(a) }
func (a Params) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a Params) Less(i, j int) bool { return a[i].First < a[j].First }


// CalcSignature 计算签名
func CalcSignature(querys map[string]string, secret string) string {
	var params = Params{Pair{First: "appSecret", Second: secret}}
	for k, value := range querys {
		if k == "signature" {
			continue
		}

		if k == "appSecret" {
			log.Default().Sugar().Errorf("querys can't contains appSecret")
			continue
		}
		params = append(params, Pair{First: k, Second: value})
	}

	sort.Sort(params)
	sb := strings.Builder{}
	for _, p := range params {
		if sb.Len() > 0 {
			sb.WriteString(",")
		}
		sb.WriteString(p.First)
		sb.WriteString("=")
		sb.WriteString(p.Second)
	}

	str := sb.String()

	// log.Default().Sugar().Debugf("To be calc sha1 sum sting: %s, querys: %v", str, querys)

	sum := fmt.Sprintf("%x", sha1.Sum([]byte(str)))
	return sum
}

```

### request example

- allyid := 206
- Secret := xxxxx

```
 curl "http://127.0.0.1:15000/api/ally/report?allyid=206&begDate=2021-03-19&endDate=2021-03-20&signature=5938c6a7a8b7b3b065977bb3f0fb54799c8fad6a"
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
      "revenue": 17.2642
    },
    {
      "date": "2021-04-24",
      "impressions": 408999,
      "revenue": 44.0067
    }
  ]
}

```

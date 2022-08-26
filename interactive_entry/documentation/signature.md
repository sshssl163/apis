# HTTP URL signature

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
1. 4 common form params in all HTTPs GET request (appKey, deviceId, nonce, timestamp)
2. The appSecret key used for calcing signature
3. Other form parameters enumerated in each API

* There are three step:
1. get appKey and appSecret from our dashboard(ask your BD for Account), each publisher has its own appKey and appSecret.
2. Combine all of the parameters to a <b>stringToCalcSignature</b>. The format follow: key=value[,key1=value1]. <b> Keys are sorted by dict order. </b>
3. use sha1.sum(<b>stringToCalcSignature</b>) calc signature

* check signature please use: https://api.adfly.vn/devtools/sign

## eg
```
http://api.flygame.io/api/ig/sdk/init?appKey=vnntest0529&demoKey=xxx&deviceId=1011925844&language=vn&network=wifi&nonce=dOauHY&publisher=vnntest0529&signature=84f10b82133320bdba3bcd469c5ae5da6f60ab03&timestamp=1638848308372&widgetId=131

Appsecret: 9a19fab1935aba50f1fd5a6bdb442172
```

## code


### 计算signature

```golang
import (
	"crypto/sha1"
	"fmt"
	"net/url"
	"sort"
	"strings"

	"github.com/xbonlinenet/goup/frame/log"
	"github.com/xbonlinenet/goup/frame/util"
)

type Pair struct {
	First  string
	Second string
}

type Params []Pair

func (a Params) Len() int           { return len(a) }
func (a Params) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a Params) Less(i, j int) bool { return a[i].First < a[j].First }

var letters = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")

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

### 获取最终参数
```golang
import (
	"fmt"
	"math/rand"
	"time"
)

var letters = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")

func randSeq(n int) string {
	b := make([]rune, n)
	for i := range b {
		b[i] = letters[rand.Intn(len(letters))]
	}
	return string(b)
}

func FormatParams(querys map[string]string, key, secret string, deviceId string) map[string]string {
	querys["appKey"] = key
	querys["deviceId"] = deviceId
	querys["nonce"] = randSeq(6)
	querys["timestamp"] = fmt.Sprintf("%d", time.Now().UnixNano()/1000/1000)

    sig := CalcSignature(querys, secret)
	querys["signature"] = sig

	return querys
}
```

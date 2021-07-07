import Base64 from 'crypto-js/enc-base64';
import Utf8 from 'crypto-js/enc-utf8';
import crypto from 'crypto-js';
import sha1 from 'crypto-js/sha1';
import zlib from 'react-zlib-js';

const HOST_URL = 'host';
const APP_KEY = 'app-key';
const APP_SECRET = 'app-secret';

function generateGzip(input) {
    return new Promise((resolve, reject) => {
        zlib.gzip(input, function (error, result) {
            if (error) {
                reject(error);
                return;
            }
            resolve(result.toString('base64'));
        });
    });
}

function generateSignature(ci, deviceId, nonce, soltId, timestamp) {
    const content = `appKey=${APP_KEY},appSecret=${APP_SECRET},ci=${ci},deviceId=${deviceId},nonce=${nonce},soltId=${soltId},timestamp=${timestamp}`;
    return sha1(content).toString();
}

function request() {
    const clientInfo = {};
    clientInfo.advertiserId = advertisingId; // android
    clientInfo.idfa = idfa; // ios
    clientInfo.os = 'android_6.0.1';
    clientInfo.network = 'wifi';
    clientInfo.widgetId = '851';


    return generateGzip(JSON.stringify(clientInfo))
        .then(compressedCI => {
            const now = new Date().getTime();
            const nonce = '123456'; // random 6 bytes
            const soltId = 1
            const signature = generateSignature(
                compressedCI,
                advertisingId,
                nonce,
                soltId,
                now,
            );

            // build request with params
            const requestURL =
                `${HOST_URL}?` +
                `appKey=${APP_KEY}&` +
                `ci=${encodeURIComponent(compressedCI)}&` +
                `deviceId=${encodeURIComponent(advertisingId)}&` +
                `nonce=${encodeURIComponent(nonce)}&` +
                `soltId=${soltId}&` +
                `timestamp=${now}&` +
                `signature=${signature}`;

            // send request
            return fetch(requestURL, { method: 'GET' });
        })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if (data.code === 0) {
                const { landing, widgetTracker, assertInterval, assertList } = data;
                return {
                    landing,
                    widgetTracker,
                    assertInterval,
                    assertList,
                };
            }
            throw new Error(data.message ? data.message : 'Error');
        });
}


// {
//     "code": 0,
//         "message": "",
//             "landing": "https://game.flygame.io/tha_qa/index.html?bx_third_client=2048-match to win&uid=&v=1.1.11&req_id=8da77ec1-7747-432c-a85c-b2a8495a3fd2&assert=&pid=25&theme=2&showMore=1&wid=990&inner=0&country=th&ad
//     "widgetTracker": {
//         "impl": "https://api.flygame.io/api/ig/widget/stat?gameType=tha_qa&platform=android&appKey=sys&partner=2048-match+to+win&wid=990&assertId=0&entry_alg=&landing_alg=ctr_top5_rand&country=th&widgetId=990&timest2a8495a3fd2&deviceId=&nonce=396576182&signature=2e9eeca2ab88409e63bf27f45d9f9f6802458fa0",
//             "click": "https://api.flygame.io/api/ig/widget/stat?timestamp=1625566077257&action=3&landing_alg=ctr_top5_rand&gameType=tha_qa&deviceId=&nonce=1361858605&platform=android&partner=2048-match+to+win&assertId=0ntry=th&entry_alg=&widgetId=990&wid=990&signature=768ddfad47b33aebc45eb612be59d80838e71ce4",
//                 "close": "https://api.flygame.io/api/ig/widget/stat?timestamp=1625566077257&wid=990&partner=2048-match+to+win&gameType=tha_qa&platform=android&appKey=sys&nonce=789402386&assertId=0&entry_alg=&landing_alg=ctrtry=th&deviceId=&action=5&widgetId=990&signature=ab18bfb3ea08dbf6e11d7910c1dbc2528e096c0b",
//                     "UserIp": "127.0.0.1"
//     },
//     "assertInterval": 10,
//         "assertList": [
//             {
//                 "assertId": "24020",
//                 "img": "https://image2.vnay.vn/topnews-2017/imgs/2b/0a/2b0aad173b425b5c99ed600d19882caa.gif",
//                 "tracker": {
//                     "impl": "https://api.flygame.io/api/ig/widget/stat?action=2&platform=android&timestamp=1625566077255&assertId=24020&req_id=8da77ec1-7747-432c-a85c-b2a8495a3fd2&gameType=&landing_alg=&country=th&widgetId=in&wid=990&deviceId=&nonce=1226493728&signature=5aa2fda3a76d7cecb0158cb6cabc03dc28573090",
//                     "click": "https://api.flygame.io/api/ig/widget/stat?jas=1&assertId=24020&platform=android&widgetId=990&appKey=sys&country=th&req_id=8da77ec1-7747-432c-a85c-b2a8495a3fd2&landing_alg=&partner=2048-match+toon=3&entry_alg=stat&gameType=&wid=990&signature=a4eb2369c4e4664f77c6d49e062723ab7a5cc957",
//                     "close": "https://api.flygame.io/api/ig/widget/stat?jas=1&action=5&partner=2048-match+to+win&country=th&req_id=8da77ec1-7747-432c-a85c-b2a8495a3fd2&widgetId=990&landing_alg=&nonce=1186184854&timestamp=16oid&gameType=&assertId=24020&deviceId=&signature=477982346e78139c6fcc953d1175cac7cceb82e6",
//                     "UserIp": "127.0.0.1"
//                 }
//             }
//         ]
// }
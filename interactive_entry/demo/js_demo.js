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
		zlib.gzip(input, function(error, result) {
			if (error) {
				reject(error);
				return;
			}
			resolve(result.toString('base64'));
		});
	});
}

function generateSignature(ci, deviceId, nonce, timestamp) {
	const content = `appKey=${APP_KEY},appSecret=${APP_SECRET},ci=${ci},deviceId=${deviceId},nonce=${nonce},timestamp=${timestamp}`;
	return sha1(content).toString();
}

function request() {
	const clientInfo = {};
	clientInfo.advertiserId = advertisingId; // android
	clientInfo.idfa = idfa; // ios
	clientInfo.os = 'android_6.0.1';
	clientInfo.network = 'wifi';

	return generateGzip(JSON.stringify(clientInfo))
		.then(compressedCI => {
			const now = new Date().getTime();
			const nonce = '123456'; // random 6 bytes
			const signature = generateSignature(
				compressedCI,
				advertisingId,
				nonce,
				now,
			);

			// build request with params
			const requestURL =
				`${HOST_URL}?` +
				`appKey=${APP_KEY}&` +
				`ci=${encodeURIComponent(compressedCI)}&` +
				`deviceId=${encodeURIComponent(advertisingId)}&` +
				`nonce=${encodeURIComponent(nonce)}&` +
				`timestamp=${now}&` +
				`signature=${signature}`;

			// send request
			return fetch(requestURL, { method: 'GET' });
		})
		.then(response => response.json())
		.then(data => {
			console.log(data);
			if (data.imageUrl) {
				const { imageUrl, landingUrl, reportExposeUrl, reportClickUrl } = data;
				return {
					imageUrl,
					landingUrl,
					reportClickUrl,
					reportExposeUrl,
				};
			}
			throw new Error(data.message ? data.message : 'Error');
		});
}

let BASE_URL = ''


if (process.env.NODE_ENV == 'development') {
    BASE_URL = 'http://127.0.0.1:8080/xgszp-boot' // 开发环境
} else {
	BASE_URL = 'http://192.168.101.12:8080/xgszp-boot' // 生产环境
}
let staticDomainURL = BASE_URL+ '/sys/common/static';

const configService = {
	apiUrl: BASE_URL,
	staticDomainURL: staticDomainURL
};

console.log('当前环境:', process.env.NODE_ENV, 'BASE_URL:', BASE_URL)

export default configService

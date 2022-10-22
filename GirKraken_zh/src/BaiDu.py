import time
import json
import hashlib
import jsonpath
import requests


class BaiDu(object):

    def __init__(self, toLanguage = "zh", fromLanguage = "auto"):
        # Baidu翻译开放平台个人的 appid 和 key
        self.appid = ""
        self.key = ""

        self.lg_from = fromLanguage
        self.lg_to = toLanguage
        self.salt = str(int(time.time()))
        self.query = ""  #请求翻译的数据
        self.sign = ""   #验证数据的sign值
        self.apiUrl = "https://fanyi-api.baidu.com/api/trans/vip/translate"

    def getSign(self, appid, query, salt, key):
        MD5 = hashlib.md5()
        signString = ''.join([appid, query, salt, key])
        MD5.update(signString.encode())
        return MD5.hexdigest()

    def getFormData(self):
        formData = {
            "q": self.query,
            "from": self.lg_from,
            "to": self.lg_to,
            "appid": self.appid,
            "salt": self.salt,
            "sign": self.sign,
            "action": 1  #自定义术语库是否干预API（是/否：1/0）
        }
        return formData

    def getHeaders(self):
        headers = {
            "User-Agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.47",
            "Referer": "https://fanyi.baidu.com/"
        }
        return headers

    def wait(self, QPS):
        if 0 < QPS <= 10:
            waitTime = 1/QPS
        else:
            waitTime = 0.1  #不符合规则默认最大速度
        time.sleep(waitTime)

    def getResponse(self, post_form):
        response = requests.post(url=self.apiUrl, data=post_form, headers=self.getHeaders())
        data = json.loads(response.content)
        try:
            result = jsonpath.jsonpath(data, "$..dst")[0]
        except TypeError:
            result = ''
        except:
            result = None
        return result

    def run(self, query):
        self.query = query
        self.sign = self.getSign(self.appid, self.query, self.salt, self.key)
        formData = self.getFormData()
        result = self.getResponse(post_form=formData)
        self.wait(QPS=10) #每秒访问次数，请设置在10以内
        return result

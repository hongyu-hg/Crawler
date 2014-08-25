package com.hg.crawler.test;

import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hg.crawler.PageFetcherCommon;

public class TestXpath {
	@Test
	public void testXpath1() throws Exception {
		PageFetcherCommon fetcher = new PageFetcherCommon();
		fetcher.setUrl("http://www.360buy.com/product/1000234560.html");
		NodeList nodeList = fetcher.getNodeList("//*[@id=\"detail\"]");
		System.out.println(nodeList.getLength());
		if (nodeList.getLength() > 0) {
			System.out.println(nodeList.item(0).getTextContent());
		}
	}

	@Test
	public void testReplace() throws ClientProtocolException, IOException, SAXException, XPathExpressionException {
		String s = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"Ptable\" width=\"100%\" xmlns=\"http://www.w3.org/1999/xhtml\"><TBODY><tr><th class=\"tdTitle\" colspan=\"2\">主体</th></tr><tr/><tr><td class=\"tdTitle\">品牌</td><td>科龙（KELONG）</td></tr><tr><td class=\"tdTitle\">系列</td><td>节能明星</td></tr><tr><td class=\"tdTitle\">型号</td><td>KFR-23GW/UG-1(K18)</td></tr><tr><td class=\"tdTitle\">颜色</td><td>银灰色</td></tr><tr><td class=\"tdTitle\">类别</td><td>壁挂式</td></tr><tr><th class=\"tdTitle\" colspan=\"2\">功能</th></tr><tr/><tr><td class=\"tdTitle\">制冷类型</td><td>冷暖</td></tr><tr><td class=\"tdTitle\">匹数</td><td>小1匹</td></tr><tr><td class=\"tdTitle\">定频/变频</td><td>定频</td></tr><tr><td class=\"tdTitle\">能效等级</td><td>2级</td></tr><tr><td class=\"tdTitle\">电辅加热</td><td>支持</td></tr><tr><td class=\"tdTitle\">适用面积(平方米)</td><td>10-16</td></tr><tr><td class=\"tdTitle\">制冷量(W)</td><td>2300</td></tr><tr><td class=\"tdTitle\">制冷功率(W)</td><td>657</td></tr><tr><td class=\"tdTitle\">制热量(W)</td><td>2530</td></tr><tr><td class=\"tdTitle\">制热功率(W)</td><td>657</td></tr><tr><td class=\"tdTitle\">电辅加热功率(W)</td><td>800</td></tr><tr><td class=\"tdTitle\">内机噪音(dB(A)</td><td>23-34db(A)</td></tr><tr><td class=\"tdTitle\">外机噪音(dB(A)</td><td>49db(A)</td></tr><tr><td class=\"tdTitle\">定频机能效比</td><td>3.50</td></tr><tr><td class=\"tdTitle\">循环风量(m3/h)</td><td>520</td></tr><tr><td class=\"tdTitle\">除湿量(×10-3m3/h)</td><td>0.9</td></tr><tr><th class=\"tdTitle\" colspan=\"2\">规格</th></tr><tr/><tr><td class=\"tdTitle\">电源（V/hz）</td><td>220V/50Hz</td></tr><tr><td class=\"tdTitle\">内机尺寸（宽x高x深）mm</td><td>818*270*202mm</td></tr><tr><td class=\"tdTitle\">外机尺寸（宽x高x深）mm</td><td>760*540*255mm</td></tr><tr><td class=\"tdTitle\">内机重量（kg）</td><td>9kg</td></tr><tr><td class=\"tdTitle\">外机重量（kg）</td><td>32kg</td></tr><tr><th class=\"tdTitle\" colspan=\"2\">特性</th></tr><tr/><tr><td class=\"tdTitle\">特性</td><td>亚克力镜面VLED显示屏<br/>多重复合健康滤网<br/>和旋美音：舞动自由心情<br/>夜光遥控器<br/>“双高效”专用压缩机</td></tr></TBODY></table>";
		System.out.println(s.replaceAll("(<[a-zA-Z]+) [^>]+", "$1"));
	}

	@Test
	public void testXpath2() throws Exception {
		PageFetcherCommon fetcher = new PageFetcherCommon();
		fetcher.setUrl("http://www.newegg.com.cn/BrandList.htm");
		NodeList nodeList = fetcher.getNodeList("//a");
		System.out.println(nodeList.getLength());
		if (nodeList.getLength() > 0) {
			System.out.println(nodeList.item(0).getTextContent());
		}
	}
}

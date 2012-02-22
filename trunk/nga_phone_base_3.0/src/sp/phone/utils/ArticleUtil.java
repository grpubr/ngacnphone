package sp.phone.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.PageAttribute;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import sp.phone.bean.Article;
import sp.phone.bean.ArticlePage;
import sp.phone.bean.User;

@SuppressWarnings("unused")
public class ArticleUtil {

	public static ArticlePage parserArticleList(String html)
			throws ParserException {

		AndFilter andFilter = new AndFilter(new TagNameFilter("table"),
				new HasAttributeFilter("class", "forumbox postbox"));
		AndFilter andFilter2 = new AndFilter(new TagNameFilter("span"),
				new HasAttributeFilter("class", "page_nav"));

		AndFilter andFilter3 = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("id", "m_nav"));

		OrFilter orFilter = new OrFilter();

		orFilter.setPredicates(new NodeFilter[] { andFilter, andFilter2,
				andFilter3 });
		Parser myParser = new Parser(html);
		NodeList nodeList = myParser.parse(orFilter);

		ArticlePage articlePage = new ArticlePage();

		List<Article> listArticle = new ArrayList<Article>();

		for (Node node : nodeList.toNodeArray()) {
			if (node instanceof TableTag) {
				TableTag table = (TableTag) node;
				TableRow[] rows = table.getRows();
				if (rows.length == 1) {
					Article article = new Article();
					User user = new User();
					TableRow tr0 = (TableRow) rows[0];
					TableColumn[] tds0 = tr0.getColumns();
					TableColumn td0 = tds0[0];
					Div div0 = null;
					LinkTag lt = null;
					if( td0.getChild(1) instanceof Div)
					{
						div0 = (Div)td0.getChild(1);
						lt = (LinkTag) div0.getChild(1);
					}else{//XXX's reply
						continue;
						//lt = (LinkTag)td0.getChild(1);
					}
					String url = div0.getStringText();	
					url = url.substring(url.indexOf("<a id='pid") + "<a id='pid".length());
					url = url.substring(0,url.indexOf("Anchor'></a>"));
					if(! "0".equals(url))
						url = "bbs.ngacn.cc/read.php?pid="+url;
					
					article.setUrl(url);
					//String floor = td0.getChild(1).getChildren().toNodeArray()[0].getText();
					
					//floor = lt.getStringText();
					String floor=  lt.getChild(0).getText();
					floor = floor.substring(1, floor.length() - 3);
					article.setFloor(Integer.parseInt(floor));
					LinkTag l = (LinkTag) div0.getChild(3);
					String nickName = l.getChild(1).getText();
					user.setNickName(nickName);

					String userId = l.getLink().split("uid=")[1];
					// System.out.println(userId);
					user.setUserId(userId);

					TableColumn td1 = tds0[1];
					String lastPostTime = "";
					/*Div avastarDiv = (Div)((Span)td0.getChild(2)).getChild(0);
					String avatarImage = avastarDiv.getChild(0).getText();
					avatarImage = avatarImage.substring(avatarImage.indexOf("urlc("+4),avatarImage.indexOf(")"));*/
					
					String content ="" ;
					Node td1Children[] = td1.getChildrenAsNodeArray();
					for (Node node3 : td1Children) {
						if (node3 instanceof Span) {
							Span sss = (Span) node3;

							if(sss.getAttribute("id") != null && sss.getAttribute("id").startsWith("postcontent") )
							{
								content = content + sss.getStringText();//sss.getFirstChild().getText();
								//break;
							}
						}else if( node3 instanceof HeadingTag){
							HeadingTag ht = (HeadingTag)node3;
							String  titleID = ht.getAttribute("id");
							if(titleID != null && !titleID.equals("postsubject0") && ht.getChildCount() !=0){
								String title =ht.getChild(0).getText() ;
								article.setTitle(title);
							}
								
						}else if ( node3 instanceof ImageTag){
							ImageTag imgtag = (ImageTag)node3;
							String avatarStr = imgtag.getAttribute("onerror");
							String avatarImage = "";
							if (avatarStr != null &&avatarStr.startsWith("commonui.postDisp") && avatarStr.indexOf("http://") != -1)
							{
								avatarStr = avatarStr.substring(avatarStr.indexOf("http://"));
								avatarImage = avatarStr.substring(0,avatarStr.indexOf("\""));
								user.setAvatarImage(avatarImage);
							}
						}else if( node3 instanceof Div){
							Div div3 = (Div)node3;
							String postDate = div3.getStringText();
							int start = postDate.indexOf("'postdate");
							if( start != -1){
								start += "'postdate".length() ;
								start = postDate.indexOf('>', start) + 1;
								int end = postDate.indexOf("</span>", start);
								if (end == -1)
									end = postDate.length();
								postDate = postDate.substring(start,end);
								 article.setLastTime(postDate);
							}
						}
					}
					
					article.setContent(content);


					article.setUser(user);
					listArticle.add(article);
				}
			} else if (node instanceof Div) {
				Div div = (Div) node;
				Node[] links =  div.getChild(1).getChildren()
						.toNodeArray();
				LinkTag linkTag = null;
				for(Node linknode:links){
					if(linknode instanceof LinkTag){
						LinkTag tmp = (LinkTag) linknode;
						if(!tmp.getLink().equals("")){
							linkTag = tmp;
						}
					}
					
				}
				HashMap<String, String> current = new HashMap<String, String>();
				current.put("link", linkTag.getLink());
				current.put("title", linkTag.getLinkText());
				articlePage.setNow(current);
			} else if (node instanceof Span) {
				Span span = (Span) node;
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

				HashMap<String, String> page = new HashMap<String, String>();
				for (Node node2 : span.getChildren().toNodeArray()) {
					if (node2 instanceof LinkTag) {
						LinkTag linkTag = (LinkTag) node2;

						if (StringUtil.isNumer(linkTag.getLinkText())) {
							if ("b current".equals(linkTag
									.getAttribute("class"))) {
								page.put("current", linkTag.getLink());
								page.put("num", linkTag.getLinkText());
							}
							HashMap<String, String> hashMap = new HashMap<String, String>();
							hashMap.put("link", linkTag.getLink());
							hashMap.put("num", linkTag.getLinkText());
							list.add(hashMap);
						} else {
							if ("&lt;&lt;".equals(linkTag.getLinkText())) {
								page.put("first", linkTag.getLink());
							} else if ("&lt;".equals(linkTag.getLinkText())) {
								page.put("prev", linkTag.getLink());
							} else if ("&gt;".equals(linkTag.getLinkText())) {
								page.put("next", linkTag.getLink());
							} else if ("&gt;&gt;".equals(linkTag.getLinkText())) {
								page.put("last", linkTag.getLink());
							}
						}
					}
				}
				articlePage.setPage(page);
				articlePage.setList(list);

			}
		}
		if(listArticle.size() == 0)
			articlePage = null;
		else
			articlePage.setListArticle(listArticle);
		return articlePage;
	}
}

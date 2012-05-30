package sp.phone.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class StringUtil {
	private final static String HOST = "http://bbs.ngacn.cc/";
	private static final String lesserNukeStyle = "<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>";
	private static final String endDiv = "</div>";
	
	/** 验证是否是邮箱 */
	public static boolean isEmail(String email) {
		String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(pattern1);
		Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			return false;
		} else {
			return true;
		}
	}

	/** 判断是否是 "" 或者 null */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isEmpty(StringBuffer str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}

	/** yy-M-dd hh:mm */
	public static Long sDateToLong(String sDate) {
		DateFormat df = new SimpleDateFormat("yy-M-dd hh:mm");
		Date date = null;
		try {
			date = df.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static boolean isNumer(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static Long parseLong(String str) {
		if (str == null) {
			return null;
		} else {
			if (str.equals("")) {
				return 0l;
			} else {
				return Long.parseLong(str);
			}
		}
	}

	public static Long sDateToLong(String sDate, String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = df.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}



	public static String parseHTML(String s) {
		// 转换字体
		if (s.indexOf("[quote]") != -1) {
			s = s.replace("[quote]", "");
			s = s
					.replace("[/quote]",
							"</font><font color='#1d2a63' size='10'>");

			s = s.replace("[b]", "<font color='red' size='1'>");
			s = s.replace("[/b]", "</font>");
			s = s.replace("<br/><br/>", "<br/>");
			s = s.replace("<br/><br/>", "<br/>");

			s = s.replace("[/pid]", "<font color='blue' size='2'>");
			s = s + "</font>";
		} else {
			s = "<font color='#1d2a63' size='10'>" + s;
			s = s + "</font>";
		}
		// 转换 表情

		s = s.replaceAll("(\\[s:\\d\\])", "<img src='$1'>");
		return s;
	}

	public static String decodeForumTag(String s) {
		if(s==null)
			return "";
		//quote
		String quoteStyle = "<div style='background:#E8E8E8;border:1px solid #888' >";
		if(ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT)
			quoteStyle = "<div style='background:#000000;border:1px solid #888' >";
		
		final String styleLeft = "<div style='float:left' >";
		final String styleRight = "<div style='float:right' >";
		
		s = s.replaceAll("\\[l\\]", styleLeft);
		s = s.replaceAll("\\[/l\\]", endDiv);
		s = s.replaceAll("\\[L\\]", styleLeft);
		s = s.replaceAll("\\[/L\\]", endDiv);
		
		s = s.replaceAll("\\[R\\]", styleRight);
		s = s.replaceAll("\\[/R\\]", endDiv);
		
		final String styleAlignRight = "<div style='text-align:right' >";
		final String styleAlignLeft = "<div style='text-align:left' >";
		s = s.replaceAll("\\[align=right\\]", styleAlignRight);
		s = s.replaceAll("\\[align=left\\]", styleAlignLeft);
		s = s.replaceAll("\\[/align\\]", endDiv);
		
		s = s.replaceAll("\\[quote\\]",quoteStyle);
		s = s.replaceAll("\\[/quote\\]", endDiv);
		//reply
		s = s.replaceAll(
				"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
		
		//topic
		s = s.replaceAll(
				"\\[tid=\\d+\\]Topic\\[/pid\\]", "Topic");
		//reply
		//s = s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
		//		, "Reply to Reply <b>$1</b>");
		// 转换 tag
		s = s.replaceAll("\\[b\\]", "<b>");
		s = s.replaceAll("\\[/b\\]","</b>"/* "</font>"*/);
		
		s = s.replaceAll("\\[u\\]", "<u>");
		s = s.replaceAll("\\[/u\\]","</u>");
		
		s = s.replaceAll("\\[s:(\\d+)\\]", "<img src='file:///android_asset/a$1.gif'>");
		//[url][/url]
		s = s.replaceAll("\\[url\\](http[^\\[|\\]]+)\\[/url\\]",
				"<a href=\"$1\">$1</a>");
		s = s.replaceAll("\\[url=(http[^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]"
				,"<a href=\"$1\">$2</a>");
		//flash
		s = s.replaceAll("\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
				"<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");
		//color
		
		s = s.replaceAll("\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
				,"<b style=\"color:$1\">$2</b>");
		
		
		//lessernuke
		s = s.replaceAll("\\[lessernuke\\]", lesserNukeStyle);
		s = s.replaceAll("\\[/lessernuke\\]", endDiv);
		
		s = s.replaceAll("\\[table\\]","<table style='color:green'>");
		s = s.replaceAll("\\[/table\\]","</table>");
		s = s.replaceAll("\\[tr\\]","<tr>");
		s = s.replaceAll("\\[/tr\\]","<tr>");
		s = s.replaceAll("\\[td\\]",
				"<td style=\"border:1px solid red\">");
		s = s.replaceAll("\\[/td\\]","<td>");
		//[i][/i]
		s = s.replaceAll("\\[i\\]", "<i style=\"font-style:italic\">");
		s = s.replaceAll("\\[/i\\]", "</i>");
		//[del][/del]
		s = s.replaceAll("\\[del\\]", "<del class=\"gray\">");
		s = s.replaceAll("\\[/del\\]","</del>");
		
		s = s.replaceAll("\\[font=(\\w+?)\\]","<span style=\"font-family:$1\">");
		s = s.replaceAll("\\[/font\\]","</span>");
		
		s = s.replaceAll("\\[/font\\]","</span>");
		s = s.replaceAll("\\[/font\\]","</span>");
		
		s = s.replaceAll("\\[size=(\\d+)%\\]","<span style=\"font-size:$1%;line-height:$1%\">");
		s = s.replaceAll("\\[/size\\]","</span>");
		
		//[img]./ddd.jpg[/img]
		s = s.replaceAll("\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]", 
				"<a href='http://img.ngacn.cc/attachments$1'><img src='http://img.ngacn.cc/attachments$1' style= 'max-width:100%;' ></a>");
		s = s.replaceAll("\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]", 
				"<a href='$1'><img src='$1' style= 'max-width:100%;' ></a>");
		
		s = s.replaceAll("\\[IMG\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/IMG\\]", 
				"<a href='http://img.ngacn.cc/attachments$1'><img src='http://img.ngacn.cc/attachments$1' style= 'max-width:100%;' ></a>");
		s = s.replaceAll("\\[IMG\\]\\s*(http[^\\[|\\]]+)\\s*\\[/IMG\\]", 
				"<a href='$1'><img src='$1' style= 'max-width:100%;' ></a>");
		
		
		return s;
	}

	public static String removeBrTag(String s){
		s =s.replaceAll("<br/>", "\n");
		return s;
	}
	/**
	 * 处理URL
	 * 
	 * @param url
	 * @return
	 */
	public static String doURL(String url) {
		if (!url.startsWith(HOST)) {
			return HOST + url;
		} else {
			return url;
		}
	}

	public static String getSaying() {
		Random random = new Random();
		int num = random.nextInt(SAYING.length);
		return SAYING[num];
	}

	public static String unEscapeHtml(String s){
		String ret = "";
		ret = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(s);
		return ret;
	}
	

	
	public static String buildThreadURLByTid(String tid){
		return "/read.php?tid="+tid;
	}
	private static final String[] SAYING = {
			"战争打响，真理阵亡。;埃斯库罗斯",
			"让子弹先走。 ",
			"人类不结束战争，战争就会结束人类。;约翰·F·肯尼迪",
			"战争无法决定谁是正确的，只能决定谁是存活的一方。;罗素 ",
			"没有海军陆战队的军舰就像没有扣子的衣服。 ;美国海军上将 大卫·D·波特",
			"传媒是我们主要的思想武器。;赫鲁晓夫",
			"不管你喜不喜欢，历史都站在我们这边，而你们将被我们埋葬！;赫鲁晓夫",
			"敌人在的射程内，彼此彼此。;步兵日记 ",
			"枚战斧巡航导弹的造价：90万美元。 ",
			"一架F-22猛禽战斗机的造价：1.35亿美元",
			"一架F-117A“夜鹰”隐形战斗机的造价：1.22亿美元 ",
			"一架B-2轰炸机的造价：22亿美元",
			"只要有人类存在，就会有战争 。;爱因斯坦 ",
			"对着敌人瞄准。;美国 火箭发射器上的使用说明 ",
			"任何诚实的军事指挥官都会承认他在动用军队时犯下过错误。;罗伯特·麦克纳马拉",
			"你可以用暴力获得权利，但你会如坐针毡。;叶利钦 ",
			"世上最致命的武器是陆战队和他的枪！;美国将军 约翰 J. 珀欣",
			"站在理想一边的人不能称为恐怖分子。;阿拉法特",
			"没有比挨了枪子还安然无恙更爽的了。 ;丘吉尔",
			"对于没经历过战争的人来说，战争当然是轻松愉快的。;伊拉斯谟 ",
			"友军伤害——不友好。",
			"就像士兵们结束战争一样，外交官往往是开始战争的关键。",
			"科技在被应用之前，都是道德中立的。用之善则善，用之恶则恶。;威廉·吉布森",
			"老家伙们宣战，上前线的却是小伙子们。;赫伯特·胡佛 第三十一位美国总统",
			"战场上的指挥官总是对的，屁股后面的司令部总是错的，除非举出反例。;鲍威尔 美国前国务卿",
			"自由并不是免费的，但是美国海军陆战队会帮你出大头。",
			"我不知道第三次世界大战人类会用什么武器，但第四次世界大战会是棍棒和石头。 ;爱因斯坦",
			"你总是知道该做什么，但难的是去做。",
			"知己知彼，百战不殆。;孙子",
			"几乎所有人都能矗立在逆境中，但是如果你想知道一个人的真实一面，请给他力量。;林肯",
			"如果我们不能向国家证明我们的理想更有价值，我们最好重新检视我们的推理,罗伯特·麦克纳马拉",
			"自由之树必须用暴君和爱国者的鲜血一遍又一遍的洗刷;托马斯·杰斐逊 (美国政治家, 第三任总统, indpdc宣言的起草人)",
			"如果机翼飞得比机身还快，那八成是架直升机——所以这玩意不安全。",
			"5秒的引信只烧3秒;步兵日记",
			"如果进攻太过顺利，那么恭喜你上当了。;士兵日记 ",
			"没有任何作战计划在与敌人遭遇后还有效。;鲍威尔",
			"当保险丝被拔掉后，手雷先生就不再是我们的朋友了。;美国陆军训练提示",
			"人有生死，国有兴亡，而意志长存。;肯尼迪",
			"枚标枪反坦克导弹的造价：8万美元 ",
			"以道作人主，不以兵强于天下",
			"如果你记不住，66式(XD)就是冲着你来的 ",
			"只有两种人了解陆战队：陆战队和它的敌人，其他人都在扯二手淡。",
			"我身边的陆战队员越多，我越Happy 。;美国陆军克拉克将军 ",
			"别忘了，你的武器是由出价最低的竞标商制造的。 ",
			"记住要透过现象看本质。不要因为害怕真相的肮脏而退缩。",
			"嘿，看开点，他们也许没子弹了。;士兵日记",
			"这个世界不会接受专政与支配。;戈尔巴乔夫 ",
			"暴君们总会有些微不足道的美德，他们会支持法律，然后摧毁它。;伏尔泰 ",
			"英雄不见得比别人更勇敢，但他们多坚持了5分钟。;里根",
			"最后，很幸运，我们曾“如此”接近核战争，但避免了。;罗伯特·麦克纳马拉",
			"有些人活了一辈子，一直希望干些什么大事，但陆战队员们没有那个问题。 ;里根",
			"一般来讲，直接降落在刚刚被轰炸过的区域是不明智的。 ;美国空军中将",
			"我们之所以能够在床上睡安稳觉，是因为大兵们正在为我们站岗。;乔治·奥韦尔",
			"如果你一开始没搞定，赶快呼叫空中支援。 ",
			"曳光弹照亮的不光是敌人。;美国陆军条例",
			"团队协作很重要，它能让别人替你挨枪子。",
			"只有和平才会获得最终的胜利。 ;爱默生",
			"在一个恐怖主义可能拥有科技的世界里，如果我们不采取行动，将会十分后悔。;康多莉扎·赖斯 (美国第66任国务卿) ",
			"兵者，诡道也。;孙子",
			"人类的可靠性和核武器，这对不稳定的组合会毁灭许多国家。;罗伯特·麦克纳马拉",
			"在战争中，输赢、生死只是一念之差。 ;道格拉斯·麦克阿瑟将军",
			"你不能说文明没有进步——至少在每次战争中，他们都换种新方法来干掉你。",
			"在你明白核武器怎么用之前，这玩意就能毁灭国家了。;罗伯特·麦克纳马拉",
			"指挥的家伙不配当英雄，真正的英雄在战斗中诞生。",
			"任何合格的士兵都应该反对战争，同时，也有着值得为之战斗的东西。",
			"不想打胜仗就别去送死。 ",
			"难知如阴，动如雷震。;孙子",
			"衷心想参加战争的人，肯定没真正体验过。;拉里 瑞福斯",
			"说“笔强于剑”的人肯定没见过自动武器。;道格拉斯·麦克阿瑟将军",
			"邪恶的得逞依靠善良的无为 ;埃德蒙·伯克",
			"If a man has done his best, what else is there?;乔治 S. 巴顿将军",
			"手雷的爆炸半径总是比你的跳跃距离多一点。",
			"暴君们一边夸耀他如何爱民，一边在残害百姓。",
			"每个暴君都相信自由——他自己的自由。;阿尔伯特·哈伯德(美国作家)",
			"相对于战争结束来说，我们更希望所有的战争本就没有爆发。;富兰克林·D·罗斯福",
			"成功不是终点，失败也不是终结，只有勇气才是永恒。;温斯顿·丘吉尔 ",
			"没有必胜的决心，战争必败无疑。;道格拉斯·麦克阿瑟 ",
			"所有的战争都是内战，因为所有的人类都是同胞。;弗朗索瓦·费奈隆 ",
			"在战争中，第二名是没有奖赏的。;奥玛·布莱德利将军 ",
			"好动与不满足是进步的第一必需品。",
			"time is money",
			"不论多么师出有名，也决不能因此误以为战争是无罪的。;厄尼斯．海明威",
			"为已死的人哀悼是愚蠢的，我们反而应该感谢上帝曾经赐予他生命。;乔治．巴顿将军",
			"战争的目的不是要你为国牺牲，而是要让该死的敌人为他的国家牺牲。;乔治．巴顿将军",
			"一死则百了 － 没有人，就不会有战争。;约瑟夫．斯大林",
			"一个人的死亡是天大的不幸，而数百万人的死亡则只是简单的统计数字。;约瑟夫．斯大林",
			"幸好战争是如此地丑恶，否则我们恐怕会爱上它。;罗伯特．李",
			"我自己都忍不住开始鼓掌直到我意识到自己是桑德兰的主席。 ;Peter Reid,在博格坎普对桑德兰的比赛中入球后",
			"我告诉我儿子Josh “霍华德·威尔金森希望爸爸为英格兰队比赛。”他把这件事告诉了我的女儿Olivia，然后他们的眼中都含着泪水问我‘那是不是说明你不再为阿森纳踢球了？;Lee Dixon ",

			"战士会愿意为了一小块勋章而奋战到底。;拿破仑．波拿巴特",
			"害怕战败的人一定会战败。;拿破仑．波拿巴特",
			"绝不可与同一敌人对峙太久，否则他会学会你所有的战术。;拿破仑．波拿巴特",
			"不怀念苏联的人没心没肺，想重回苏联的人无头无脑。;普京",
			"...和......表示的含义是不同的。",
			"闭嘴！我们正在讨论民主。",
			"某男：你说这个世界上有没有男的有两个蛋蛋？",
			"1024",
			"你懂的！",
			"YSLM",
			"5楼:people don't want face,sky down no enemy.",
			"1楼:no 废死,who's your 爹地.",
			"Your brain has two parts:the left&the right .Your left brain has nothing right,and your right brain has nothing left.",
			"现在找一个又傻又善良又漂亮,身材又好有钱又肯倒贴的女人怎么这么难?", "8楼:美国的乡下人英语都这么好，难怪美国这么强大 ",
			"星际争霸2:目田之翼", "星际争霸2:折翼的天使 ", "鸟德怒吼:那个贼,不要一直交易我烤鹌鹑",
			"国服最新笑话:DK坦没拿盾,被踢了",
			"楼主：帮忙给我即将出生的孩子取个好听的名字。回帖：陈不悔。楼主：大哥,我姓王。回帖：我姓陈。",
			"悦来客栈是古代最大的连锁客栈。", "超级巨毒，解药，暗器都产自西域。",
			"平时朝夕相处的人，只要穿上夜行衣，再蒙个面纱，对方就不认识了。",
			"没用的小角色用的武功名字有很强的文学性和动物性，就是不大好用。", "长着超长白发+胡子的绝对是旷世高人，和他要拉好关系。",
			"英雄配一把好兵器，好到从不用去保养修理。",
			"在乱箭中，英雄要是不想死，就决不会死；万一中了箭，那也是因为一旁有大恶人挟持其亲人导致英雄分心。",
			"一定要象征性的打几下，才出绝招，并喷着口水大叫：去死吧！！",
			"使出必杀技要做很花哨的动作，还要做上一两分钟，但敌人决不会乘机偷袭，尽管这是个好机会……",
			"高手都无视万有引力，到处乱飞且飞得飞快。不过要是赶远路，却会骑马。",
			"大侠套餐：2斤熟牛肉+上等女儿红。(悦来客栈长期供应......)",
			"好人从不下毒，坏人从不不下毒；但好人从不下毒却老被诬陷下毒，坏人从不不下毒却没人怀疑他。",
			"大侠想显示自己的修为，往往会捡起一根树枝将不知天高地厚的小角色打败，后来悦来客栈开始供应树枝……",
			"在一条笔直的街道被人追杀，尽管有很多事要做，但弄翻两旁的小摊是最重要的！",
			"好人用暗器是形式所逼，多才多艺，一击必中；坏人用暗器是卑鄙无耻，旁门左道，扔死了都扔不中……",
			"坏人千心万苦扔中了，还会被好人忍着巨痛放倒，并喷着口水大叫：卑鄙！", "会有绝世佳人救起中暗器的英雄，日不久也生情……",
			"当时社会治安不好，人人佩带危险器械……", "菜市场杀猪的绝对是一胖子！！！！！",
			"绝世神兵被麻布一层一层裹紧，绝世神人也被麻布一层一层裹紧……",
			"主角一生坎坷或是一帆风顺，一生坎坷的会坎坷到死，一帆风顺的从不买票……",
			"所有人都很有钱，铜板很少出现，一张一张的银票比草纸还便宜。", " (悦来客栈的)店小二知识渊博，有问(+钱)必答！",
			"少林寺就1个方丈(老和尚那种8算)和1个徒弟厉害，其他都很菜。",
			"单挑时， “ 正义 ” 一方支撑不住了，就会喊人帮忙： “ 对付这种魔头，不用和他讲什么江湖道义，大家一起上！ ”",
			"少林图书馆经常失窃……", "一个人喝完闷酒一定会下暴雨。", "团体组合流行：四大?#，四大%￥，四大*(……",
			"拔剑时，有时会有剑气，有时会拔不出来……", "朝廷的大将军是坨屎，公公才是高手。",
			"妓院都是怡红院(我怀疑是悦来集团的子公司……)。", "美女到处都是，这是最郁闷的…… ",
			"大漩涡里不能黑小日本和AKB"

	};

	public static int getNowPageNum(String link) {
		// link: http://bbs.ngacn.cc/thread.php?fid=7&page=1&rss=1&OOXX=
		int ret = 1;
		if (link.indexOf("\n") != -1) {
			link = link.substring(0, link.length() - 1);
		}
		if (link.indexOf("&") == -1) {
			return ret;
		} else {
			try{
			ret = Integer.parseInt(link.substring(link.indexOf("page=") + 5,
					link.length()));
			}catch(Exception E){
				
			}
		}
		return ret;
	}
	final static String tips = "1.头像大小修改现在在设置里\n2.不喜欢动画的在设置里关掉\n3.在帖子里按menu可以禁止屏幕旋转，再看不到就是瞎了";
	public static String getTips(){
		
		return tips;
		
	}
}



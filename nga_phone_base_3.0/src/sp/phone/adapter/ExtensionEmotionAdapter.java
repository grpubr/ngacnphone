package sp.phone.adapter;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

import sp.phone.utils.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ExtensionEmotionAdapter extends BaseAdapter {
	private static final String dirs[] = { "baozou", "ali", "dayanmao",
			"luoxiaohei", "zhaiyin", "yangcongtou", "acniang", "bierde" };

	private static final String res[][] = {
			{
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52ecc3d7ac.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52ed6d3dc1.gif",
					"http://pic2.178.com/132/1324875/month_1206/9f20039eba5242f1f5df34779199e143.png",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52eda5044a.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52eddcb513.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52ee2f2dbe.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52eeba4771.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52ef0e1409.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52ef46b530.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52ef8788ab.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52efb178ce.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52efee430b.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52f018139f.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52f044c7b1.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52f0788e44.gif",
					"http://img.ngacn.cc/attachments/mon_201205/05/-7_4fa52f0a45e4b.gif",
					"http://pic2.178.com/132/1324875/month_1206/b66735510ea8c96938ac5d31d4c589fd.png",
					"http://pic2.178.com/132/1324875/month_1206/ccd7cbbb06e8cfa797a1070a82953b4b.png",
					"http://pic2.178.com/132/1324875/month_1207/da6eeba218ce65317a625c60a6314bd2.png",
					"http://pic2.178.com/132/1324875/month_1207/15ffc0dc16d79e8229c0a7e20f7dc86b.png",
					"http://pic2.178.com/132/1324875/month_1207/6175b8ea9cd71127142824e8f799714b.png",
					"http://pic2.178.com/132/1324875/month_1207/9696572c3280071e7d27fb15f072ed94.png",
					"http://pic2.178.com/132/1324875/month_1207/890b262fcc2834e79037cfbdde177bac.png",
					"http://pic2.178.com/132/1324875/month_1207/7f16fd71d6b86d9a90c0eea23a207420.png",
					"http://pic2.178.com/132/1324875/month_1207/f50a6b4a25bcd0550395f4b9fbec7e12.png",
					"http://pic2.178.com/132/1324875/month_1207/74bdbd3f12fbfb7cb18c5e967fec3ad3.png",
					"http://pic2.178.com/132/1324875/month_1207/d207310836cebda7b967498d30cd9cd1.png",
					"http://pic2.178.com/132/1324875/month_1207/a055d2ae7c3700d9dcfb3307dd954e02.png",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501659f095f86.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501659f46c92c.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501659f74f065.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501659fb92e28.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501659fef2d35.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_50165a095609b.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_50165a0c8f964.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016817445bdb.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016817c3f714.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501681825a85d.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501681873b6a9.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016818c54525.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_50168192b5000.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016819980fbc.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016819dcc612.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501681a579a75.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501681aa5d92a.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501686cde01cb.jpg",
					"http://img.ngacn.cc/attachments/mon_201202/04/-447601_4f2d1e80ab343.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016ae12ae284.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_5016ae1989baf.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045aeafd7372.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017a342b27.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017a6ec30d.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017ad66045.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017b09a8c3.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017b484549.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017ef537ea.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017f2f26a0.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017f5b84d6.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017f85b1b1.jpg",
					"http://img.ngacn.cc/attachments/mon_201209/12/-47218_505017fb0e250.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501686d6c3bff.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501686d1e6d55.jpg",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501686c890c75.jpg",
					"http://img.ngacn.cc/attachments/mon_201208/14/-7_502a289ca1256.jpg",
					"http://pic2.178.com/79/799003/month_1207/28c5e844157dcd4c932427f8c9d3f51a.png",
					"http://pic2.178.com/79/799003/month_1207/ffd6126dfa9a5994fef30f83a60c2650.png",
					"http://pic2.178.com/79/799003/month_1207/8c57d3de7a471cf98e035476b4685579.png",
					"http://pic2.178.com/79/799003/month_1207/f9cc7ce52e6381d468a511b88d34d70c.png",
					"http://pic2.178.com/79/799003/month_1207/4e691d95d3d811a85c55d705df8aced0.png",
					"http://pic2.178.com/79/799003/month_1207/b712d971a5fc63afaaed6354d91e376c.png",
					"http://pic2.178.com/79/799003/month_1207/e70ae1e7346619b4b7266dbd02b73ca7.png",
					"http://pic2.178.com/79/799003/month_1207/38e2db572feae5b33c20ea6f229c3759.png",
					"http://pic2.178.com/79/799003/month_1207/b8169c2bbbe4665b4bf6f586a6ea2717.png",
					"http://pic2.178.com/79/799003/month_1207/c9963cd620e2ff683c5939c071f2746c.png",
					"http://pic2.178.com/79/799003/month_1207/a107ccf5274bf3c837ffb7909bd0d469.png",
					"http://pic2.178.com/79/799003/month_1207/790acfd25773a45e527a723b2f6a40d4.png",
					"http://pic2.178.com/79/799003/month_1207/08eee2e216d922b507eefb3318c64c77.png",
					"http://pic2.178.com/79/799003/month_1207/791175d2f3c7005753c26956e99a9470.png",
					"http://pic2.178.com/79/799003/month_1207/565a693819be4f62e7ee36b02c69ef70.png",
					"http://pic2.178.com/79/799003/month_1207/889bafb318fbe216141eac72e0706f5b.png",
					"http://pic2.178.com/79/799003/month_1207/1070c0f99fa75975e56a99a4f21707f0.png",
					"http://pic2.178.com/130/1301667/month_1207/6cc10a4dba106faa171a36d691dac6bb.gif",
					"http://pic2.178.com/27/278913/month_1207/7a2302f1845a0979df055bdb699cdede.png",
					"http://pic2.178.com/27/278913/month_1207/abefd9e4c03ff38d467b6d771ea2e857.png",
					"http://pic2.178.com/27/278913/month_1207/a0315c1460ff654da9b6e56ac34b5fac.png",
					"http://pic2.178.com/27/278913/month_1207/bde7b8d04f40b2748137801729660a5e.png",
					"http://pic2.178.com/27/278913/month_1207/dd44d413faa2a2e2505837c5fefc4f21.png",
					"http://pic2.178.com/27/278913/month_1207/a43dd40a03c7726f56960123361021aa.png",
					"http://pic2.178.com/27/278913/month_1207/31a3f5da298b101a4fc020ed8cda451b.png",
					"http://pic2.178.com/27/278913/month_1207/ada5425f0263b65ffae1c9348a3c5d30.png",
					"http://pic2.178.com/27/278913/month_1207/ebc20db6eebd5dd7f44106cebd63449f.png",
					"http://pic2.178.com/27/278913/month_1207/4a0809e874350c2b76b251a98569df6a.png",
					"http://pic2.178.com/27/278913/month_1207/4f7b4d5981b1f00ec7923b6eba5a985c.png",
					"http://pic2.178.com/27/278913/month_1207/a165e0caf3ec1cccacb6dc3031ac9662.png",
					"http://img.ngacn.cc/attachments/mon_201207/12/-7_4ffe8f28503c0.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffbda6559fd.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffbdad17999.png",
					"http://pic2.178.com/27/278913/month_1207/243ea3433f225fe948dbb8c1c3462a70.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffa1319197d.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffa4c03caa1.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffaa9d30dac.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffaaa52673e.png",
					"http://img.ngacn.cc/attachments/mon_201207/13/-7_4fffc468eb93e.png",
					"http://img.ngacn.cc/attachments/mon_201207/16/-7_5003c666c1113.png",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_501674e61bdc7.png",
					"http://img.ngacn.cc/attachments/mon_201207/30/-7_50167f81bba1c.gif",
					"http://img.ngacn.cc/attachments/mon_201207/16/-7_5003ceb826aa6.gif",
					"http://pic2.178.com/132/1324875/month_1207/862cbe0df668b5df0d4a08f20960c810.gif" },

			// /ali
			{
					"http://www.a-li.com.cn/upload_files/27/1_20101216121201_kmxzc.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121251_sjha7.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121238_8svki.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121228_2jegy.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121249_6mz0s.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121238_3pzvr.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121226_c9i4w.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121214_bxwtc.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121259_f6vla.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121246_s9xod.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121232_oqoye.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121216_8zwaw.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121203_ib3kx.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121248_mqcqp.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121238_mhrq0.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121226_jqga5.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121259_3mbgu.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121210_afr2s.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121256_fyu75.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121240_ifin9.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121224_6lgrh.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121238_f9dkr.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121224_ekpbw.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121208_kwqcf.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121213_neebm.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121215_yupro.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216111242_avobq.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215191236_pt1zu.gif",
					"http://www.a-li.com.cn/upload_files/other/1_20101215191201_af8w0.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181250_m6suf.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181216_rq3zj.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181201_lodof.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181240_2vg7r.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181251_lmouj.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181209_5l98i.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181226_y5yzx.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181234_xa6c8.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181202_ep3s6.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181231_6sb8g.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181233_qdpd7.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181253_9koqr.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181208_6gbop.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181247_h2r42.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181231_mvmo1.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181204_vgjtz.gif",
					"http://www.a-li.com.cn/upload_files/other/1_20101215181254_bvmvp.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215181204_uliqa.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171259_yapsu.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171219_vpdrc.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171259_zdcds.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171241_7addj.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171222_vk3u9.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171217_sumji.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171213_gejmj.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171247_l7zqw.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171230_6m8os.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171207_ea1yz.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171240_xowel.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171258_iykdj.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171258_3bbsl.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171241_pxcfu.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171219_rf4oo.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171258_tiequ.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171244_qvo9v.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171220_i8a2i.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171224_j1e0n.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171207_kjule.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171210_odnfn.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171250_ylov8.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215171247_fkucp.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161256_7sduk.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161209_2lmz3.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161258_3s2ec.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161230_prme4.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101224161243_t0eeg.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161243_szsjd.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161227_doj0x.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161206_5bzpm.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215161253_8gpea.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101215151254_etb4h.gif",
					"http://www.a-li.com.cn/upload_files/other/1_20101215121217_lllmj.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101108141137_hxwrq.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101108141118_69yhl.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101108131105_a3ezo.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101108131131_iom12.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101108131126_o0pwe.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121254_whk5b.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20101216121220_wuuq9.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110118100122_wr7dl.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110118120136_cjbkn.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110628110632_d1mgn.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110719100741_mmk37.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110308110334_2ikqe.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110315140335_yhecr.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20120112100130_dy9h4.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110607120645_sg4sl.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110607120659_thiql.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110614160644_gbair.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110712150729_sjlwu.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110809120802_gezct.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110816170805_rzmrv.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110823100803_exvsf.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110913160938_6mgea.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110921180950_alri0.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20110927120937_iccty.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20111206171238_d0fz3.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20111214141200_3wqjb.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20111228161253_agis9.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20111228161245_ngwpv.gif",
					"http://www.a-li.com.cn/upload_files/27/1_20120112100106_wsuum.gif",
					"http://www.a-li.com.cn/upload_files/27/3_20120619180609_wgndr.gif",
					"http://www.a-li.com.cn/upload_files/27/3_20120626160625_cxl8x.gif" },
			// dayanmao
			{
					"http://pic1.178.com/36/364011/month_1001/78aa7a031f2f880692ba41047cf0efd4.gif",
					"http://pic1.178.com/36/364011/month_1001/e979814adb039319e185c0693f70ac19.gif",
					"http://pic1.178.com/36/364011/month_1001/c71b0666ae7360fb57ca7e4746d77180.gif",
					"http://pic1.178.com/36/364011/month_1001/65a10d35bae4a645c4e1fdd0c4a72b7c.jpg",
					"http://pic1.178.com/36/364011/month_1001/fd4d129f46c9d45265fa548aae1e1a46.gif",
					"http://pic1.178.com/36/364011/month_1001/88f65c29aab14a27613e64b77a1ce787.gif",
					"http://pic1.178.com/36/364011/month_1001/7dc7db2cbc60e4b69e5616f344f81de4.gif",
					"http://pic1.178.com/36/364011/month_1001/9a530ab7c625abf240a4683622c8835c.gif",
					"http://pic1.178.com/36/364011/month_1001/f88ac0bc61e839d578269e193710a926.gif",
					"http://pic1.178.com/36/364011/month_1001/86fbc79540b5b76225d910d7324e6dcc.gif",
					"http://pic1.178.com/36/364011/month_1001/6b6fb853e8ec2c96ad5fab40610f27a7.gif",
					"http://pic1.178.com/36/364011/month_1001/bbf7db0849de605351c2493db50e0043.gif",
					"http://pic2.178.com/132/1324875/month_1206/8379f982088ed8ddd2c9d19a67cb5c73.gif",
					"http://pic2.178.com/132/1324875/month_1206/54bb6b6331125af57b67884117d202c7.gif",
					"http://pic2.178.com/132/1324875/month_1206/83eddfc33830e9325fc69fc60e782861.gif",
					"http://pic2.178.com/132/1324875/month_1207/ab7d2da8db56b8477031b089c94c2042.gif",
					"http://pic2.178.com/132/1324875/month_1207/0aca3c8ecb51090c14ee9a3e3330e293.gif",
					"http://pic2.178.com/132/1324875/month_1207/3d125f40932a525e0a0049a675c70776.gif",
					"http://pic2.178.com/132/1324875/month_1207/96dad75c7889dddb47f03058437412f0.gif",
					"http://pic2.178.com/132/1324875/month_1207/08cb2c1a86e85f77824510e31266dca6.gif",
					"http://pic2.178.com/132/1324875/month_1207/806b7c7e3e036d6373605e88ed2e3370.gif",
					"http://pic2.178.com/132/1324875/month_1207/b8e7f7ed55260626d179c375c709abc5.gif",
					"http://pic2.178.com/132/1324875/month_1207/e84f7a8605042643c0a67462187b0e92.gif",
					"http://pic2.178.com/132/1324875/month_1207/103cbe0ee09a2dcbe476c5b48056c3b4.gif",
					"http://pic2.178.com/132/1324875/month_1207/7b2bcfa3c2a30c32915fbd5d9f746156.gif",
					"http://pic2.178.com/132/1324875/month_1207/2afc2006a6a817ec1dd577956450c72c.gif",
					"http://pic2.178.com/132/1324875/month_1207/60f602c812c5d9b0f88f5b78a89e914e.gif" },
			// luoxiaohei
			{
					"http://pic2.178.com/825/8256664/month_1206/5c0de79888729eff15d9e987fbba4c25.gif",
					"http://pic2.178.com/825/8256664/month_1206/d29ac462d5ccbfdd286f6eb90bea71cf.gif",
					"http://pic2.178.com/825/8256664/month_1206/9e21cdefd6b3f92d0b1f4bba3e4cda38.gif",
					"http://pic2.178.com/825/8256664/month_1206/54b82214e99cfa3a3da7a66afe864fdf.gif",
					"http://pic2.178.com/825/8256664/month_1206/db828196a219ca5c516bbeafd47117f4.gif",
					"http://pic2.178.com/825/8256664/month_1206/cc308c269384b073a91e8e851e9471f9.gif",
					"http://pic2.178.com/825/8256664/month_1206/62508130547e05e4c322839968dd216e.gif",
					"http://pic2.178.com/825/8256664/month_1206/65164a8305f446ed5b82e7c43ff43901.gif",
					"http://pic2.178.com/825/8256664/month_1206/5d8f39679c3e6d295a0305fc23d9af2a.gif",
					"http://pic2.178.com/825/8256664/month_1206/33a65a5d1ea1aa9cc77db3d9f94bc9d6.gif",
					"http://pic2.178.com/825/8256664/month_1206/d54f27747487bf59649b5da25ce52963.gif",
					"http://pic2.178.com/825/8256664/month_1206/70e129baaf95e2f56f3976104f893473.gif",
					"http://pic2.178.com/825/8256664/month_1206/c34f57dc27a585b0c842f788140b85ca.gif",
					"http://pic2.178.com/825/8256664/month_1206/fa8bc2db7c485e1ecaecfa639363e9af.gif",
					"http://pic2.178.com/825/8256664/month_1206/289fe14a25dff087f17ddddedcbad510.gif",
					"http://pic2.178.com/825/8256664/month_1206/89450c4f7eb2dfe3ea3832be12d9a1fd.gif",
					"http://pic2.178.com/825/8256664/month_1206/93799e7cf5b4c32a6186718c17cc6488.gif",
					"http://pic2.178.com/825/8256664/month_1206/46c6f342a8b051a38ea3bc8ff959e02d.gif",
					"http://pic2.178.com/825/8256664/month_1206/52e06e3401a72311cb40941beeed3590.gif",
					"http://pic2.178.com/825/8256664/month_1206/6f7972b1a2452f968f1a6e14aef81248.gif",
					"http://pic2.178.com/825/8256664/month_1206/9ea2d7fb2fb9f9f8cb8e110b5b5a0bd8.gif",
					"http://pic2.178.com/825/8256664/month_1206/343f7b3c71da2975c4582b1e713f75fc.gif",
					"http://pic2.178.com/825/8256664/month_1206/da77ab1bc23dc5718f286006b2c4d873.gif",
					"http://pic2.178.com/825/8256664/month_1206/fa3ecaa87375e10a09996e663eb9c7c9.gif",
					"http://pic2.178.com/825/8256664/month_1206/560cc871dacea718be53a968fe99e25b.gif",
					"http://pic2.178.com/825/8256664/month_1206/3acf42b1de0c59cad6f2bc57c5013d2e.gif",
					"http://pic2.178.com/825/8256664/month_1206/1ac323e705d6e359066ab2ea8c9dded1.gif",
					"http://pic2.178.com/825/8256664/month_1206/4f13fb2e09b9024ee320a3d0fedfad3e.gif",
					"http://pic2.178.com/825/8256664/month_1206/433df28f586354c47395623f5a5a8f5c.gif",
					"http://pic2.178.com/825/8256664/month_1206/ac1fa35081de0adfabc70789f5fa5103.gif",
					"http://pic2.178.com/825/8256664/month_1206/4fce62ce80196ecd0a2d72ebffba7152.gif",
					"http://pic2.178.com/825/8256664/month_1206/e6be0e1f5d16216ecbaf4af96d61e951.gif",
					"http://pic2.178.com/825/8256664/month_1206/68d091a8525af89af3792ceffc0c88d7.gif",
					"http://pic2.178.com/825/8256664/month_1206/2f298fd226b5c1b9c8cd1d7976b725bc.gif",
					"http://pic2.178.com/825/8256664/month_1206/0ed9b744030c1f2a66b0ba11a1098271.gif",
					"http://pic2.178.com/825/8256664/month_1206/768dc8efd0b5e2539f8591ecff8357a9.gif",
					"http://pic2.178.com/825/8256664/month_1206/56b3ffe62f4beaa13d41a239d1c8a51f.gif",
					"http://pic2.178.com/825/8256664/month_1206/64824c081c08d2cdf410a2c91b0ac548.gif" },
			// zhaiyin
			{
					"http://pic2.178.com/724/7247152/month_1201/614d41768bfa6ccfd5f09b94d38350ae.png",
					"http://pic2.178.com/344/3445603/month_1202/f267d6e1e968b22829ae84def74383ee.gif",
					"http://pic2.178.com/344/3445603/month_1202/8b30432cf1e996e5d54da1ce73ddf412.gif",
					"http://pic2.178.com/344/3445603/month_1202/e27d85f225459a81190c7c4f88d8a059.gif",
					"http://pic2.178.com/724/7247152/month_1201/86c8a51602d075a094b99b12e990df3b.png",
					"http://pic2.178.com/130/1301667/month_1203/a2e39025c4b1616529d25139db825c51.gif",
					"http://pic2.178.com/130/1301667/month_1203/c7757f253cebf2b6852740dcdd2c2577.gif",
					"http://pic2.178.com/130/1301667/month_1203/8fb244b66fbe432e7c8057fabe25a92b.gif",
					"http://pic2.178.com/724/7247152/month_1201/0844552367c35f2441e9cd311cbd1907.png",
					"http://pic2.178.com/724/7247152/month_1201/14549eb96bb1cc17b56ced51950b86ef.png",
					"http://pic2.178.com/130/1301667/month_1203/9fc702d34f09d0e5a34a7e91826105ca.gif",
					"http://pic2.178.com/130/1301667/month_1203/c60351097d3766dcea25cdb4d73f5924.gif",
					"http://pic2.178.com/724/7247152/month_1201/c1310062db8df7a975cbebd7c81e8061.png",
					"http://pic2.178.com/344/3445603/month_1202/71c4d2261a4c3e76fea0ab6a1355baf3.gif",
					"http://pic2.178.com/130/1301667/month_1203/ac28b3cc5dbd85e96a09ee347a2e8f54.gif",
					"http://pic2.178.com/130/1301667/month_1203/0bf39dd8614dd1d240eadc426038ac15.gif",
					"http://pic2.178.com/130/1301667/month_1203/cb995a1b73d741db306318c3c37101aa.gif",
					"http://pic2.178.com/130/1301667/month_1203/55c354f3cf70e2c383cfad743c7e2a1e.gif",
					"http://pic2.178.com/724/7247152/month_1202/b138b30c679345c54770f6a053e5f964.png",
					"http://pic2.178.com/130/1301667/month_1203/e475039c32aee23393517cffba66cdd8.gif",
					"http://pic2.178.com/130/1301667/month_1203/7765fb6834a209365dd14cad527f8540.gif" },
			// yangcongtou
			{
					"http://pic1.178.com/36/364011/month_1009/58f2e9793ffee208f3d7a81817402792.gif",
					"http://pic1.178.com/36/364011/month_1009/3163e1167ec7a623fe517fbc3054e43a.gif",
					"http://pic1.178.com/36/364011/month_1009/0955538fb820b45be4144316e254712d.gif",
					"http://pic1.178.com/36/364011/month_1009/142398b4df954afd4190a20be99b8203.gif",
					"http://pic1.178.com/36/364011/month_1009/b0f398f7319a673fdaeb9598f8f099f1.gif",
					"http://pic1.178.com/36/364011/month_1009/609a083bd2b92343d04490003700e9a5.gif",
					"http://pic1.178.com/36/364011/month_1009/efe0a1df7f30efe2b37c316cc758821e.gif",
					"http://pic1.178.com/36/364011/month_1009/16e50694a733d4e14b1c22c527e2fc1c.gif",
					"http://pic1.178.com/36/364011/month_1009/0d1ab68e99c319ee8d8f4f1f444a3005.gif",
					"http://pic1.178.com/36/364011/month_1009/ece65081c7cdf5fd971381c7828463ac.gif",
					"http://pic1.178.com/36/364011/month_1009/0562892d8c98f56bd209905365f4744f.gif",
					"http://pic1.178.com/36/364011/month_1009/6663253a32c24702221e04fc15941152.gif",
					"http://pic1.178.com/36/364011/month_1009/21b5d40c93e77202b6fddd6fcb58c716.gif",
					"http://pic1.178.com/36/364011/month_1009/6e18c8910c2153a14bb1f03739d26984.gif",
					"http://pic1.178.com/36/364011/month_1009/05089eed6ba7c039ba4d170399d05175.gif",
					"http://pic1.178.com/36/364011/month_1009/c8e42af6e8f78fb4ca834692dd201dbc.gif",
					"http://pic1.178.com/36/364011/month_1009/5a6fc407afe71590a7ddff6c3efab6b5.gif",
					"http://pic1.178.com/36/364011/month_1009/754daf85416056de7994b313e547416b.gif",
					"http://pic1.178.com/36/364011/month_1009/646adb1f2e3e8d3c1c4c085dcae0553e.gif",
					"http://pic1.178.com/36/364011/month_1009/c7d66ddf07ffb3bbf7e7aed5e21632dd.gif",
					"http://pic1.178.com/36/364011/month_1009/722d1751bd74ea166e8542249308d0c8.gif",
					"http://pic1.178.com/36/364011/month_1009/6cc1214e9f305973b930dc43e2af5cd2.gif",
					"http://pic1.178.com/36/364011/month_1009/e622bc29c6dd1bc9dd62908f96f6ed83.gif",
					"http://pic1.178.com/36/364011/month_1009/379cc8a35c65bffddbe8360313b0308c.gif",
					"http://pic1.178.com/36/364011/month_1009/23fde534f4aa05706053146325225e3c.gif",
					"http://pic1.178.com/36/364011/month_1009/4f24cef2c142d0eae2b257299d693f91.gif",
					"http://pic1.178.com/36/364011/month_1009/4eb01bce60fd876ccd85e846bfbc8f59.gif",
					"http://pic1.178.com/36/364011/month_1009/ad4801b5fdc354ab14fc7e5c45703c9c.gif",
					"http://pic1.178.com/36/364011/month_1009/01858ad018f38a083b3aa1193788ad54.gif",
					"http://pic1.178.com/36/364011/month_1009/b24b6877bdd5d917aad8c666cd74c192.gif",
					"http://pic1.178.com/36/364011/month_1009/1d38a16e512ffbed023fc99dcf255d70.gif",
					"http://pic1.178.com/36/364011/month_1009/8ecd34c43a627c7e7d910d8d611f7f1e.gif",
					"http://pic1.178.com/36/364011/month_1009/410f18f0418f9c9fcf64f3fc2d2873cc.gif",
					"http://pic1.178.com/36/364011/month_1009/fb9726e183bcee0cc59084aaa7679579.gif",
					"http://pic1.178.com/36/364011/month_1009/918721bf6b646ceeb8b4b2871ad99ac4.gif",
					"http://pic1.178.com/36/364011/month_1009/f91b805e1f5ba581fec197a0d37700f5.gif",
					"http://pic1.178.com/36/364011/month_1009/2fcd15b411018745d2eb0403cfeec579.gif",
					"http://pic1.178.com/36/364011/month_1009/a2da6616a08dc4f31d32ee8a7c1bd6a2.gif",
					"http://pic1.178.com/36/364011/month_1009/13ba79897ec11f717b7529dda7e1ea28.gif",
					"http://pic1.178.com/36/364011/month_1009/46d7d3811d7cc9569330d6742679d34f.gif",
					"http://pic1.178.com/36/364011/month_1009/02395d95a9562a390720a3834ff78a9b.gif",
					"http://pic1.178.com/36/364011/month_1009/c02866240926eb881a99c73586c78ee9.gif",
					"http://pic1.178.com/36/364011/month_1009/55b461153cdfb0dabecb5ae032584552.gif",
					"http://pic1.178.com/36/364011/month_1009/d4f2fe179a03eb79b66110737b54c33b.gif",
					"http://pic1.178.com/36/364011/month_1009/dda125a8417a57236c8ee668544f1641.gif",
					"http://pic1.178.com/36/364011/month_1009/a0d89a475c28ac91649d0ddf9e2a0758.gif",
					"http://pic1.178.com/36/364011/month_1009/46b3a1a3bc1bd9fb688e255fa7e9b86d.gif",
					"http://pic1.178.com/36/364011/month_1009/0efdab411472b2e6c4713dbfcbbf6a90.gif",
					"http://pic1.178.com/36/364011/month_1009/fab5cb3ae1ecfd4269a5e121c7ada6fc.gif",
					"http://pic1.178.com/36/364011/month_1009/9ba9bee0b1a0b6fdeb2298023b136d24.gif",
					"http://pic1.178.com/36/364011/month_1009/360af520e2ab1bcbc0a1ca7cba5c114a.gif",
					"http://pic1.178.com/36/364011/month_1009/2062c2ebf2046d04f85cbe6bc22e4843.gif",
					"http://pic1.178.com/36/364011/month_1009/371a5dcdfed3c4b87e71558555ce8036.gif",
					"http://pic1.178.com/36/364011/month_1009/b66cfd9c713d043bf70be194285cc9fa.gif",
					"http://pic1.178.com/36/364011/month_1009/b3e128e016d14757dd671d38faf83f1f.gif",
					"http://pic1.178.com/36/364011/month_1009/7ed02eb56d178ab1560a5008415baa1f.gif",
					"http://pic1.178.com/36/364011/month_1009/454b128c938ba943d83ee77bae0a6e94.gif",
					"http://pic1.178.com/36/364011/month_1009/626821eb753d621eaf1ce05fd373e5ac.gif",
					"http://pic1.178.com/36/364011/month_1009/125447028f381c52147ecbfb1546ef97.gif",
					"http://pic1.178.com/36/364011/month_1009/eba571d0399258e9ff449bf5f3d294c3.gif",
					"http://pic1.178.com/36/364011/month_1009/7ac1128bc4f7c4f288a96e6afa97e6d1.gif",
					"http://pic1.178.com/36/364011/month_1009/5cc711746112411f1b007c60c6997a42.gif",
					"http://pic1.178.com/36/364011/month_1009/517b1df1fec94d264851e5ab4c5e55ab.gif",
					"http://pic1.178.com/36/364011/month_1009/203b6aadea70f7bfeecd190447443474.gif",
					"http://pic1.178.com/36/364011/month_1009/8cc46510d9a98a188eed862b4d7d04cc.gif",
					"http://pic1.178.com/36/364011/month_1009/9979946f3249c995fed0ef34d4dd4dfe.gif",
					"http://pic1.178.com/36/364011/month_1009/39214e07051856d10c792338966ebd85.gif",
					"http://pic1.178.com/36/364011/month_1009/b2b031d1d65d80e8a4a53347f3449f8b.gif",
					"http://pic1.178.com/36/364011/month_1009/f38827dd474df2382616daddc1f0631e.gif",
					"http://pic1.178.com/36/364011/month_1009/5e66b3021dfafdb31893e10598d1aa21.gif",
					"http://pic1.178.com/36/364011/month_1009/9a5b5a5528a21fa695f303bbfb5fc3ac.gif",
					"http://pic1.178.com/36/364011/month_1009/3e0f7254ad8389a41c81d68d2c2b440a.gif",
					"http://pic1.178.com/36/364011/month_1009/859165bb9e49af6237766dd1b1241f12.gif",
					"http://pic1.178.com/36/364011/month_1009/8e2ffc2a03b5750044f1075e31c3a601.gif",
					"http://pic1.178.com/36/364011/month_1009/65dccd1dc4ff43a5e5d703ce28a1ba9a.gif",
					"http://pic1.178.com/36/364011/month_1009/81a5c30f0ddeca2d5055b1859606df97.gif",
					"http://pic1.178.com/36/364011/month_1009/0bdef507f13f063d33dee730b3a8495e.gif",
					"http://pic1.178.com/36/364011/month_1009/972f0ef9bff622cbcfb89657d8eebf80.gif",
					"http://pic1.178.com/36/364011/month_1009/d1838578aa71bb964d097163a87552c2.gif",
					"http://pic1.178.com/36/364011/month_1009/ca52a9f4617bbc39fdb84de519e17b37.gif" },
			// acniang
			{
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc4cc6331.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc4f51be7.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc521c04b.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc5579c24.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc587c6f9.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc7a0ee49.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc7d91913.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc80140e3.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc835856c.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bc8638067.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bca2a2f43.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bca55cb6e.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bca81a77f.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcaaacb45.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcad49530.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcb093870.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcb3b8944.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcb6e96d1.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcba15fcf.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcbe35760.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcdd279bc.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcdfd9c69.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bce27ab4d.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bce4f2963.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bce7cf096.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bceb823da.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcee3d6b3.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcf0ba2db.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcf37c4c9.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bcf68ddc2.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd2497822.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd27520ef.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd2a0d49a.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd2d0a416.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd2fa0790.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd330dfad.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd35aec58.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd38bdf43.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd3b4b3bd.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052bd40397e2.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c0f41d155.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c0f6da079.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c10182a21.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c104b8e27.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c1076f119.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c10aa0303.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c10d1f08c.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c1101747c.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c112b3b1b.png",
					"http://img.ngacn.cc/attachments/mon_201209/14/-47218_5052c1156ec1c.png" },
			// bierde
			{
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bd823ee71.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bd8e94feb.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bd94b6eba.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bd9d49867.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bda5828bf.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bdb19a376.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045be7051800.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045be9e16a62.gif",
					"http://pic2.178.com/101/1011736/month_1206/89d494e527d5b11a712d40fccbab5fee.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045beea312de.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bef2b42a2.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bef857bc0.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bf6234da1.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bf6a0a114.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bf6e41fe2.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bf748eded.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bf7963e3d.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bfe1e7644.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bfe95474a.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045bff13a601.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045c0026d5d6.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045c007e9a2a.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045c00be7aee.gif",
					"http://img.ngacn.cc/attachments/mon_201209/04/-47218_5045c01072fb7.gif"

			} };

	public static String getPathByURI(String uri) {
		for (int category = 0; category < res.length; category++) {
			for (int index = 0; index < res[category].length; index++) {
				if (res[category][index].equals(uri)) {
					return getFilePath(category, index);
				}
			}

		}
		return null;
	}

	private static String getFilePath(int category, int position) {
		String httpUri = res[category][position];
		String fileName = dirs[category] + "/" + FilenameUtils.getName(httpUri);
		return fileName;
	}

	final private int index;

	public ExtensionEmotionAdapter(int index) {
		super();
		this.index = index;
	}

	@Override
	public int getCount() {
		if (res.length > index) {
			String category[] = res[index];
			if (category != null)
				return category.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (index >= res.length)
			return null;
		String category[] = res[index];
		if (category == null)
			return null;
		if (position >= category.length)
			return null;

		return "[img]" + category[position] + "[/img]\n";

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view = null;
		if (convertView == null)
			view = new ImageView(parent.getContext());
		else {
			view = (ImageView) convertView;
			ImageUtil.recycleImageView(view);
		}

		InputStream is;
		try {
			is = view.getContext().getAssets().open(getFileName(position));
			Bitmap bm = BitmapFactory.decodeStream(is);
			if (bm.getWidth() > 120) {
				Bitmap resizedBm = ImageUtil.zoomImageByWidth(bm, 120);
				bm.recycle();
				bm = resizedBm;
			}
			view.setImageBitmap(bm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	private String getFileName(int position) {
		String httpUri = res[index][position];
		String fileName = dirs[index] + "/" + FilenameUtils.getName(httpUri);
		return fileName;
	}

}

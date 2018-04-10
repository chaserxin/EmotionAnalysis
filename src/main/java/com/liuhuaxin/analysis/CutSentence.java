/**
 * 本方法致力于将复杂句切分为简单句，获取每个句子的keyPart，而且目前所有的句子都还未分词，本分句方法完全可以放入Analyze类中
 */
package com.liuhuaxin.analysis;

import com.liuhuaxin.bean.Sentence;

/**
 * 句子切分
 */
public class CutSentence {
    /**
     * 本方法负责将复杂句切成简单句
     */
    public void SubSentence(String text) {
        String SentenceContent[] = text.split("[\\. 。？！? !]");//得到只有内容的单句
        int SentenceL = 0;//用于记录每一句的长度，寻找句子的标点
        for (int i = 0; i < SentenceContent.length; i++) {

            Sentence sen = new Sentence(SentenceContent[i], i);
            Sentence.ALLSENTENCES.add(sen);
            sen.participle(sen.getKeyPart());//第一次切分整段话
            SentenceL += SentenceContent[i].length();
            /**
			 * 句式分值设置
			 **/
            for (String word : sen.parts) {//如果有主观词句式为关键句u
                if (Analyse.hasfeature("idea", word)) {//发现了主观词
                    sen.setKeySentence(true);

//                    System.out.println("本句拥有主观词，是关键句");
                    Analyse.rc += "本句拥有主观词，是关键句" + "\r\n";
                }
            }
//            if (sen.getPosition() == 0 || sen.getPosition() == SentenceContent.length - 1) {//首句和尾句的句式为关键句
//                sen.setKeySentence(true);
//
//                System.out.println("本句位于一段话的首或尾");
//                Analyse.rc += "本句位于一段话的首或尾" + "\r\n";
//            }
            if (SentenceL != text.length()) {//如果最后一句没有符号，那就不用找！和？了，不然会越界
                if (text.charAt(SentenceL) == '!' || text.charAt(SentenceL) == '！') {
                    SentenceL++;
                    sen.setYeah(true);//本句有感叹号
                    sen.setQ(sen.getQ() + 2);//有感叹号分值加两分

//                    System.out.println("本句有感叹号，句式分加2");
                    Analyse.rc += "本句有感叹号，句式分加2" + "\r\n";
                } else if (text.charAt(SentenceL) == '?' || text.charAt(SentenceL) == '？') {
                    SentenceL++;
                    sen.setQuestion(true);//本句有问号
                    sen.setQ(0);

//                    System.out.println("本句有问号,句式分设为0");
                    Analyse.rc += "本句有问号,句式分设为0" + "\r\n";
                }
            }
			/*生成1阶子句*/
            if (sen.clipComma(sen.getKeyPart())) {//有逗号
                for (int i1 = 0; i1 < sen.equalParts.size(); i1++) {//在有逗号的子句中循环
                    if (sen.equalParts.get(i1).clipAnd(sen.equalParts.get(i1).getKeyPart())) {//如果发现并列句就重新调整equalParts的结构
                        int l = sen.equalParts.get(i1).equalParts.size();
                        for (int j = 0; j < l; j++) {
                            sen.equalParts.add(i1, sen.equalParts.get(i1).equalParts.get(j));//将新的并列句加入原链表
                            i1++;
                        }
                        sen.equalParts.remove(i1);//移除未切分部分
                    }
                }
                for (int i1 = 0; i1 < sen.equalParts.size(); i1++) {
//                    System.out.println("一阶子句" + i1 + "的keyPart为:" + sen.equalParts.get(i1).getKeyPart());
                    Analyse.rc += "一阶子句" + i1 + "的keyPart为:" + sen.equalParts.get(i1).getKeyPart() + "\r\n";
                }
            } else {//没有逗号则先切并列句，形成和相同的一阶子句
                sen.clipAnd(sen.getKeyPart());
            }
			
			/*进一步句子细化和情感分析*/
            if (sen.isAnd() || sen.isHascomma()) {//如果是一阶子句，则循环子句进行情感分析
                for (int i1 = 0; i1 < sen.equalParts.size(); i1++) {
                    sen.equalParts.get(i1).clipBut(sen.equalParts.get(i1).getKeyPart());//得到转折句式的keyPart
                    sen.equalParts.get(i1).clipResult(sen.equalParts.get(i1).getKeyPart());//得到因果句式的keyPart

//                    System.out.println("本句为一阶子句" + i1 + "，其keyPart为:" + sen.equalParts.get(i1).getKeyPart());
                    Analyse.rc += "本句为一阶子句" + i1 + "，其keyPart为:" + sen.equalParts.get(i1).getKeyPart() + "\r\n";

                    //简单句式则默认全部content为keyPart
                    Analyse.calculate(sen.equalParts.get(i1));
                }
            } else {//如果是0阶子句，则直接进行下一步情感分析
                sen.clipBut(sen.getKeyPart());
                sen.clipResult(sen.getKeyPart());

//                System.out.println(sen.getKeyPart());
                Analyse.rc += "本句为简单句，其keyPart为:" + sen.getKeyPart() + "\r\n";

                Analyse.calculate(sen);
            }
            sen.setCount();//计算本句分值
        }
        Analyse.rc = "";//在这里清除写入的搜索日志
    }
}
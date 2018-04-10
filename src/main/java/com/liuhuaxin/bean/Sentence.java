package com.liuhuaxin.bean;

import com.liuhuaxin.analysis.Analyse;
import com.liuhuaxin.wordsplit.WordSplitAnsj_seg;

import java.util.ArrayList;
import java.util.List;

/**
 * 句子对象
 */
public class Sentence {
    public static List<Sentence> ALLSENTENCES = new ArrayList<Sentence>(); //存放一个句子的总和
    // 所有句子分值
    public static double happy_sum = 0.0;
    public static double sad_sum = 0.0;
    // 关键句分值
    private static double key_happy_sum = 0.0;
    private static double key_sad_sum = 0.0;
    private static int keyCount = 0;

    private int position = 0;//句子在整篇文章中的位置
    private int pN = 0;//本句中否定词的位置
    private int pM = 0;//本句中程度词的位置

    private String content = "";//句子的全部内容
    private String keyPart = "";//决定本句情感的主要句子

    public List<String> parts = new ArrayList<String>();//存放句子的细粒度切分结果
    public List<Sentence> equalParts = new ArrayList<Sentence>();//存放复杂句子的切分结果
    private WordSplitAnsj_seg ws = new WordSplitAnsj_seg();

    private boolean yeah = false;//是感叹句Q=2
    private boolean question = false; //是疑问句Q=0
    private boolean and = false;//是并列句，选取分值较高的部分
    private boolean but = false;//是转折句，选取后部
    private boolean result = false;//是因果句，选取结果部分
    private boolean hascomma = false;//有逗号，处理方式和并列句相同
    private boolean keySentence = false;//判定本局是否为所有句子中的关键句

    private double happy_count = 0.0;//开心分值
    private double sad_count = 0.0;//伤心分值
    private double angry_count = 0.0;//生气分值

    private double Q = 1.0;//句式分值
    private double T = 0.0;//情感词分值
    private double M = 1.0;//程度词分值
    private int N = 0;//否定词个数
    private double P = 1.0;//否定词和程度词位置决定的共同分值

    /*构造方法*/
    public Sentence(String content, int position) {
        this.setContent(content);
        this.setPosition(position);
        this.setKeyPart(content);//先让keyPart的值和全句内容一致
    }

    /*按照逗号分句并建立子句对象数组*/
    public boolean clipComma(String text) {
        String p[] = text.split("[,，]");
        if (p.length > 1) {//确定是否有逗号

//            System.out.println("我切分了逗号");
            Analyse.rc += "我切分了逗号" + "\r\n";

            this.setHascomma(true);
            for (int i = 0; i < p.length; i++) {
                Sentence s = new Sentence(p[i], i);//创建子句对象
                s.participle(s.getKeyPart());
                equalParts.add(s);
            }
            return true;
        }
        return false;
    }

    /*按照and规则分句并建立子句对象数组*/
    public boolean clipAnd(String text) {
        String p[] = new String[2];
        int clip = 0;//记录要切分开的下标
        for (int i = 0; i < this.parts.size(); i++) {//在本句切分开的词中循环
            if (Analyse.hasfeature("f_and", parts.get(i))) {
                p[0] = text.substring(0, clip);

//                System.out.println("并列句p[0]=" + p[0]);
                Analyse.rc += "并列句p[0]=" + p[0] + "\r\n";

                p[1] = text.substring(clip + parts.get(i).length());

//                System.out.println("并列句p[1]=" + p[1]);
                Analyse.rc += "并列句p[1]=" + p[1] + "\r\n";

                break;
            } else {
                clip += parts.get(i).length();
                p[0] = text;
                p[1] = null;
            }
        }
        //通过以上过程得到了p[]这个切分好的字符串数组，接下来就是建立分句的对象
        if (p[1] != null) {//确定是否是并列句
            this.setAnd(true);
            for (int i = 0; i < p.length; i++) {
                Sentence s = new Sentence(p[i], i);//创建子句对象
                s.participle(s.getKeyPart());
                this.equalParts.add(s);
            }
            return true;
        }
        return false;

    }

    /*按照but规则分句并提取关键部分keyPart*/
    public boolean clipBut(String text) {
        String p[] = new String[2];
        int clip = 0;
        for (int i = 0; i < parts.size(); i++) {//在本句切分开的词中循环
            if (Analyse.hasfeature("but", parts.get(i))) {
                p[0] = text.substring(0, clip);

//                System.out.println("转折句p[0]=" + p[0]);
                Analyse.rc += "转折句p[0]=" + p[0] + "\r\n";

                p[1] = text.substring(clip + parts.get(i).length());

//                System.out.println("转折句p[1]=" + p[1]);
                Analyse.rc += "转折句p[1]=" + p[1] + "\r\n";

                break;
            } else {
                clip += parts.get(i).length();
                p[0] = text;
                p[1] = null;
            }
        }
        if (p[1] != null) {
            setBut(true);
            setKeyPart(p[1]);//but类型选取后面的部分
            participle(getKeyPart());//关键句改变后重新切分
            return true;
        }
        return false;

    }

    /*按照result规则分句提取关键部分keyPart*/
    public boolean clipResult(String text) {
        String p[] = new String[2];
        int clip = 0;
        for (int i = 0; i < this.parts.size(); i++) {//在本句切分开的词中循环
            if (Analyse.hasfeature("result", parts.get(i))) {
                p[0] = text.substring(0, clip);

//                System.out.println("因果句p[0]=" + p[0]);
                Analyse.rc += "因果句p[0]=" + p[0] + "\r\n";

                p[1] = text.substring(clip + parts.get(i).length());

//                System.out.println("因果句p[1]=" + p[1]);
                Analyse.rc += "因果句p[1]=" + p[1] + "\r\n";

                break;
            } else {
                clip += parts.get(i).length();
                p[0] = text;
                p[1] = null;
            }
        }
        if (p[1] != null) {
            setResult(true);
            setKeyPart(p[1]);//result类型选取后面的部分
            participle(getKeyPart());//关键句改变后重新切分
            return true;
        }
        return false;

    }

    /*切词方法，将本句中keyPart部分切开*/
    public void participle(String text) {
        parts.clear();
        parts = ws.splitSencence(text);

    }

    /*
     * 删除本次输入的所有数据
     */
    public static void clearData() {
        ALLSENTENCES.clear();

        happy_sum = 0.0;
        sad_sum = 0.0;
        key_happy_sum = 0.0;
        key_sad_sum = 0.0;
        keyCount = 0;
    }


    /*
     * get和set方法
     * */
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeyPart() {
        return keyPart;
    }

    public void setKeyPart(String keyPart) {
        this.keyPart = keyPart;
    }

    public boolean isYeah() {
        return yeah;
    }

    public void setYeah(boolean yeah) {
        this.yeah = yeah;
    }

    public boolean isQuestion() {
        return question;
    }

    public void setQuestion(boolean question) {
        this.question = question;
    }

    public boolean isAnd() {
        return and;
    }

    public void setAnd(boolean and) {
        this.and = and;
    }

    public boolean isBut() {
        return but;
    }

    public void setBut(boolean but) {
        this.but = but;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isHascomma() {
        return hascomma;
    }

    public void setHascomma(boolean hascomma) {
        this.hascomma = hascomma;
    }

    public double getHappy_count() {
        return happy_count;
    }

    public void setHappy_count(double happy_count) {
        this.happy_count = happy_count;
    }

    public double getSad_count() {
        return sad_count;
    }

    public void setSad_count(double sad_count) {
        this.sad_count = sad_count;
    }

    public double getAngry_count() {
        return angry_count;
    }

    public void setAngry_count(double angry_count) {
        this.angry_count = angry_count;
    }

    public double getQ() {
        return Q;
    }

    public void setQ(double q) {
        Q = q;
    }

    public double getT() {
        return T;
    }

    public void setT(double t) {
        T = t;
    }

    public double getM() {
        return M;
    }

    public void setM(double m) {
        M = m;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public double getP() {
        return P;
    }

    public void setP(double p) {
        P = p;
    }

    public int getpN() {
        return pN;
    }

    public void setpN(int pN) {
        this.pN = pN;
    }

    public int getpM() {
        return pM;
    }

    public void setpM(int pM) {
        this.pM = pM;
    }

    public boolean isKeySentence() {
        return keySentence;
    }

    public void setKeySentence(boolean keySentence) {
        this.keySentence = keySentence;
    }
	
	
	/*
	 * 整段话的分析方法
	 * */


    /*
     * 计算所有句子的总分
     * */
    public static void sum() {
        for (Sentence sen : ALLSENTENCES) {
            Sentence.sad_sum += sen.getSad_count();
            Sentence.happy_sum += sen.getHappy_count();
            if (sen.isKeySentence()) {//计算关键句总分
                Sentence.key_sad_sum += sen.getSad_count();
                Sentence.key_happy_sum += sen.getHappy_count();
                keyCount++;
            }
        }
    }


    public static String print() {
        String data = "";
        sum();
        data = "==============================\n" +
                "积极总分值为：" + Sentence.happy_sum + "\n" +
                "消极总分值为：" + Sentence.sad_sum + "\n" + "\n" +

                "关键句有：" + Sentence.keyCount + "句" + "\n" +
                "keySentence积极总分值为：" + Sentence.key_happy_sum + "\n" +
                "keySentence消极总分值为：" + Sentence.key_sad_sum + "\n" + "\n";
        for (int i = 0; i < ALLSENTENCES.size(); i++) {
            if (ALLSENTENCES.get(i).getHappy_count() > 0) {
                data += "句子" + i + "为积极句，分值为：" + ALLSENTENCES.get(i).getHappy_count() + "\n";
            } else if (ALLSENTENCES.get(i).getSad_count() > 0) {
                data += "句子" + i + "为消极句，分值为：" + ALLSENTENCES.get(i).getSad_count() + "\n";
            } else {
                data += "句子" + i + "的分值为：0" + "\n";
            }
        }
        return data;
    }

    /*
     * 计算单个句子的keyPart总分,分为复杂句和简单句
     * */
    public double SingleCount() {
        double s = 0.0;
        if (isAnd() || isHascomma()) {//是复杂句，具有一阶子句要优先提取出情感分值的重点句
            for (int i = 0; i < equalParts.size(); i++) {
                if (Math.abs(Math.pow(-1, equalParts.get(i).getN()) * equalParts.get(i).getM() * equalParts.get(i).getP() * equalParts.get(i).getT()) >= Math.abs(s)) {
                    s = Math.pow(-1, equalParts.get(i).getN()) * equalParts.get(i).getM() * equalParts.get(i).getP() * equalParts.get(i).getT();
                }
            }
            s *= Q; //最后再乘上母句的句式分值
            return s;
        } else {//是简单句
            s = Math.pow(-1, N) * M * P * T * Q;
        }
        return s;
    }

    /*
     * 为单个句子设置分值
     * */
    public void setCount() {
        double s = SingleCount();
        if (s > 0) {//结果是正分值，则设置为happy
            setHappy_count(s);
        } else if (s < 0) {//结果是负分值，则设置为sad
            setSad_count(-s);
        }
    }
}
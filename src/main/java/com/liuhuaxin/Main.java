package com.liuhuaxin;

import com.liuhuaxin.analysis.CutSentence;
import com.liuhuaxin.bean.Sentence;
import com.liuhuaxin.dao.DanmuDao;
import com.liuhuaxin.utli.ReadWrite;
import com.liuhuaxin.wordsplit.WordSplitAnsj_seg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 项目起始类
 */
public class Main {

    private static String FILE_NAME = "用李云龙语音在绝地求生种调戏路人.xml";
    private static WordSplitAnsj_seg ws = new WordSplitAnsj_seg();

    public static void main(String args[]) {
        long startTime = System.currentTimeMillis() / 1000;
        DanmuDao danmuDao = new DanmuDao();
        List<Map<String, Object>> contentList = danmuDao.getDanmu(FILE_NAME.split("\\.")[0]);
        int second = (int) Math.floor((Double)contentList.get(contentList.size()-1).get("time")) + 1;
        double totalEmotionScore;
        double totalPosEmotionScore = 0.0;
        double totalNegEmotionScore = 0.0;
        double[] totalEmotionScorePerSecond = new double[second];
        double[] totalPosEmotionScorePerSecond = new double[second];
        double[] totalNegEmotionScorePerSecond = new double[second];
        for (Map<String, Object> map : contentList) {
            int index = (int) Math.floor((Double)map.get("time"));
            double[] emotionScoreArr = getEmotionScore(String.valueOf(map.get("message")));
            totalPosEmotionScore += emotionScoreArr[0];
            totalNegEmotionScore -= emotionScoreArr[1];
            totalPosEmotionScorePerSecond[index] += emotionScoreArr[0];
            totalNegEmotionScorePerSecond[index] -= emotionScoreArr[1];
            totalEmotionScorePerSecond[index] += emotionScoreArr[0] - emotionScoreArr[1];
        }
        totalEmotionScore = totalPosEmotionScore + totalNegEmotionScore;
        long endTime = System.currentTimeMillis() / 1000;
        System.out.println("==========" + FILE_NAME + "==========");
        System.out.println("danmuCount: " + contentList.size());
        System.out.println("runningTime: " + String.valueOf(endTime - startTime));
        System.out.println("totalEmotionScorePerSecond: " + Arrays.toString(totalEmotionScorePerSecond));
        System.out.println("totalPosEmotionScorePerSecond: " + Arrays.toString(totalPosEmotionScorePerSecond));
        System.out.println("totalNegEmotionScorePerSecond: " + Arrays.toString(totalNegEmotionScorePerSecond));
        System.out.println("totalEmotionScore: " + totalEmotionScore);
        System.out.println("totalPosEmotionScore: " + totalPosEmotionScore);
        System.out.println("totalNegEmotionScore: " + totalNegEmotionScore);
        System.out.println("==============================");

    }


    private static double[] getEmotionScore(String content) {
        double[] emotionScoreArr = new double[2];
        CutSentence cs = new CutSentence();
        String cut = ws.splitSencenceForOutput(content);
        // save.txt中可以看到切词结果，analyserecording.txt中可以看到分析过程
        ReadWrite.write(cut+"\n", "save.txt");

        cs.SubSentence(content);
        Sentence.sum();
        emotionScoreArr[0] = Sentence.happy_sum;
        emotionScoreArr[1] = Sentence.sad_sum;
        Sentence.clearData();
        return emotionScoreArr;
    }
}

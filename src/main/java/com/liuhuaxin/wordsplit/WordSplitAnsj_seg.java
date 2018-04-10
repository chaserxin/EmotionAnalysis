package com.liuhuaxin.wordsplit;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

/**
 * Created by yuanyuan on 2018/4/1.
 */
public class WordSplitAnsj_seg {

    Forest forest = null;

    public WordSplitAnsj_seg() {
        try {
            // 加载自定义词典
            forest = Library.makeForest(WordSplitAnsj_seg.class.getResourceAsStream("/userLibrary.dic"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将每个句子单独切词,每个句子的切词结果存放在一个 sentences 列表中
     * 然后将每个句子切词后得到的 sentences 存放在一个 result 列表中
     */
    public List<List<String>> splitSencenceList(List<String> sentenceList) {
        List<List<String>> result = new ArrayList<List<String>>();

        for (String sentence : sentenceList) {
            List<String> sentences = new ArrayList<String>();
            Result wordSplitResult = DicAnalysis.parse(sentence, forest);
            for (Term term : wordSplitResult) {
                String name = term.getName();
                sentences.add(name);
            }
            result.add(sentences);
        }
        return result;
    }

    /**
     * 单独分词一个句子
     */
    public List<String> splitSencence(String sentence) {
        List<String> result = new ArrayList<String>();

        Result wordSplitResult = DicAnalysis.parse(sentence, forest);
        for (Term term : wordSplitResult) {
            result.add(term.getName());
        }
        return result;
    }

    /**
     * 单独分词一个句子
     */
    public String splitSencenceForOutput(String sentence) {
        StringBuilder sb = new StringBuilder();
        Result wordSplitResult = DicAnalysis.parse(sentence, forest);
        for (Term term : wordSplitResult) {
            sb.append(term.getName() + "|");
        }
        return sb.toString();
    }

}

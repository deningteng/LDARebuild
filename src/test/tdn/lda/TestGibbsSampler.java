package test.tdn.lda;

import java.util.Arrays;

import main.tdn.lda.GibbsSampler;

public class TestGibbsSampler {
	public static void main(String[] args){
		
		// words in documents
        int[][] documents = {
                {1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 6},
                {2, 2, 4, 2, 4, 2, 2, 2, 2, 4, 2, 2},
                {1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 0},
                {5, 6, 6, 2, 3, 3, 6, 5, 6, 2, 2, 6, 5, 6, 6, 6, 0},
                {2, 2, 4, 4, 4, 4, 1, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 0},
                {5, 4, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2}};  // 文档的词语id集合
        // vocabulary
        int V = 7;
        int K = 5;
		GibbsSampler gs = new GibbsSampler(documents,K,V);
		gs.train(2.0, 0.5);
		System.out.println("Theta Matrix");
		System.out.println(Arrays.deepToString(gs.getThetaMatrix()));
		
		System.out.println("Phi Matrix");
		System.out.println(Arrays.deepToString(gs.getPhiMatrix()));
		
	}
}

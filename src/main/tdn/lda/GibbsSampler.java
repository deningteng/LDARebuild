package main.tdn.lda;

public class GibbsSampler {
	
	private int[][] doc;
	private int K;
	private int V;
	
	private int[][] topicMatrix;//MxN
	private int[][] wordTopic;//VxK
	private int[][] docTopic;//MxK
	private int[] docWord;//1xM
	private int[] topicWord;//1xK
	
	private double[][] thetaMatrix;
	private double[][] phiMatrix;
	
	private double alpha;
	private double beta;
	
	private static int ITERATION = 500;
	private static int BURN_IN = 400;
	private static int STATISTIC_GAP = 10;
	
	private int statisticNum = 0;
	
	public GibbsSampler(int[][] doc, int K, int V){
		this.doc = doc;
		this.K = K;
		this.V = V;
		int M = doc.length;
		
		wordTopic = new int[V][K];
		docTopic = new int[M][K];
		docWord = new int[M];
		topicWord = new int[K];
		
		thetaMatrix = new double[M][K];
		phiMatrix = new double[K][V];
	}
	
	public void train(double alpha,double beta){
		this.alpha = alpha;
		this.beta = beta;
		
		initSampler();
		for(int times = 0; times < ITERATION; times++){
			gibbs();
			if(times%100 == 0){
				System.out.println("Gibbs Sampling, times = " + times);
			}
			if(times >= BURN_IN && times%STATISTIC_GAP == 0){
				updateThetaMatrix();
				updatePhiMatrix();
				statisticNum++;
			}
		}
	}

	private void updateThetaMatrix() {
		for(int index1 = 0; index1 < topicMatrix.length; index1++){
			for(int k = 0; k < K; k++){
				thetaMatrix[index1][k] += (docTopic[index1][k] + alpha)/(docWord[index1] + K*alpha);
			}
		}
	}

	private void updatePhiMatrix() {
		for(int k = 0; k < K; k++){
			for(int v = 0; v < V; v++){
				phiMatrix[k][v] +=  (wordTopic[v][k] + beta)/(topicWord[k] + V*beta);
			}
		}
	}
	
	public double[][] getThetaMatrix(){
		for(int index1 = 0; index1 < topicMatrix.length; index1++){
			for(int k = 0; k < K; k++){
				thetaMatrix[index1][k] /= statisticNum;
			}
		}
		return thetaMatrix;
	}
	
	public double[][] getPhiMatrix(){
		for(int k = 0; k < K; k++){
			for(int v = 0; v < V; v++){
				phiMatrix[k][v] /= statisticNum;
			}
		}
		return phiMatrix;
	}

	private void gibbs() {
		for(int index1 = 0; index1 < topicMatrix.length; index1++){
			for(int index2 = 0;index2 < topicMatrix[index1].length; index2++){
				int topic = topicMatrix[index1][index2];
				wordiSub(index1, index2, topic);
				topic = topicTransfer(index1,index2);
				topicMatrix[index1][index2] = topic;
				wordiAdd(index1, index2, topic);
			}
		}
		
	}

	private int topicTransfer(int index1, int index2) {
		double[] prob = new double[K];
		for(int k = 0; k < K; k++){
			double theta = (docTopic[index1][k] + alpha)/(docWord[index1] + K*alpha);
			double phi = (wordTopic[doc[index1][index2]][k] + beta)/(topicWord[k] + V*beta);
			prob[k] = theta*phi;
		}
		for(int k = 1; k < K; k++){
			prob[k] += prob[k-1];
		}
		double threshold = Math.random()*prob[K-1];
		int topic;
		for(topic = 0; topic <K; topic++){
			if(threshold < prob[topic]){
				break;
			}
		}
		return topic;
	}

	private void initSampler() {
		int M = doc.length;
		topicMatrix = new int[M][];
		for(int index1 = 0; index1 < M; index1++){
			int N = doc[index1].length;
			topicMatrix[index1] = new int[N];
			for(int index2 = 0; index2 < N; index2++){
				int topic = (int)Math.random()*K;
				topicMatrix[index1][index2] = topic;
				wordiAdd(index1, index2, topic);
			}
		}
	}
	
	private void wordiAdd(int index1, int index2, int topic){
		wordTopic[doc[index1][index2]][topic]++;
		docTopic[index1][topic]++;
		docWord[index1]++;
		topicWord[topic]++;
	}
	
	private void wordiSub(int index1, int index2, int topic){
		wordTopic[doc[index1][index2]][topic]--;
		docTopic[index1][topic]--;
		docWord[index1]--;
		topicWord[topic]--;
	}
	
	
}

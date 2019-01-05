package test.java.api;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class TestForkJoin {
 
    /**
     * 计算某数到某数的和，返回结果"和"
     */
    private static class Demo1 extends RecursiveTask<Integer> {
        private int start;
        private int end;
 
        public Demo1(int start, int end) {
            this.start = start;
            this.end = end;
        }
 
        //计算
        @Override
        protected Integer compute() {
            int sum = 0;
            if (start - end < 100) {
                for (int i = start; i < end; i++) {
                    sum += i;
                }
            } else {//间隔有100则拆分多个任务计算
                int middle = (start + end) / 2;
                Demo1 left = new Demo1(start, middle);
                Demo1 right = new Demo1(middle + 1, end);
                left.fork();
                right.fork();
 
                sum = left.join() + right.join();
            }
            return sum;
        }
    }
 
    public static void main(String[] args) throws Exception {
        Demo1 d = new Demo1(1, 10000);
        ForkJoinPool forkJoinPool = new ForkJoinPool(); //对线程池的扩展
        Future<Integer> result = forkJoinPool.submit(d);
        System.out.println(result.get());
    }
 
}

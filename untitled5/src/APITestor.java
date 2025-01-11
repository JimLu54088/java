
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class APITestor {
    public static void main(String[] args) throws Exception {
        // 获取CPU核心数
//        int coreCount = Runtime.getRuntime().availableProcessors();
//        System.out.println("CPU核心数: " + coreCount);

        long startTime = System.currentTimeMillis();

        // 定义统计变量
        AtomicInteger http200Count = new AtomicInteger();

        AtomicInteger http500Count = new AtomicInteger();


        String apiUrl = "https://XXXXXXXXXXXXi.php"; // 替换为实际 API URL

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        // 创建任务
        Callable<Void> task = () -> {
            HttpClient client = HttpClient.newHttpClient();
            for (int i = 0; i < 125000000; i++) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(apiUrl))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    // 处理响应
                    int statusCode = response.statusCode();
                    String responseBody = response.body();

                    if (statusCode == 200) {

                        http200Count.incrementAndGet();


                    } else if (statusCode == 500) {
                        http500Count.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.err.println("Error during API call: " + e.getMessage());
                }
            }
            return null;
        };

        // 提交两个任务到线程池
        Future<Void> future1 = executorService.submit(task);
        Future<Void> future2 = executorService.submit(task);
        Future<Void> future3 = executorService.submit(task);
        Future<Void> future4 = executorService.submit(task);
        Future<Void> future5 = executorService.submit(task);
        Future<Void> future6 = executorService.submit(task);
        Future<Void> future7 = executorService.submit(task);
        Future<Void> future8 = executorService.submit(task);

        // 等待两个线程完成
        future1.get();
        future2.get();
        future3.get();
        future4.get();
        future5.get();
        future6.get();
        future7.get();
        future8.get();

        // 关闭线程池
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        long executionTimeInMiniute = executionTime / 60000;

        // 打印统计结果
        System.out.println("HTTP 200 次数: " + http200Count.get());
        System.out.println("HTTP 500 次数: " + http500Count.get());
        System.out.println("Execution Time: " + executionTimeInMiniute + " (minutes)");


    }


}

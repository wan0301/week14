import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class A1113363_0512_1 {
    private static final int PORK_DUMPLINGS = 5000;
    private static final int BEEF_DUMPLINGS = 3000;
    private static final int VEGETABLE_DUMPLINGS = 1000;

    private static final int MIN_ORDER_QUANTITY = 10;
    private static final int MAX_ORDER_QUANTITY = 50;

    private static int remainingPorkDumplings = PORK_DUMPLINGS;
    private static int remainingBeefDumplings = BEEF_DUMPLINGS;
    private static int remainingVegetableDumplings = VEGETABLE_DUMPLINGS;

    private static final Object lock = new Object();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("請輸入同時光顧的顧客數目：");
        int numCustomers = scanner.nextInt();
        scanner.close();

        ExecutorService executor = Executors.newFixedThreadPool(numCustomers);

        for (int i = 0; i < numCustomers; i++) {
            executor.execute(new Customer(i + 1));
        }

        executor.shutdown();
    }

    static class Customer implements Runnable {
        private final int id;
        private final Random random;

        Customer(int id) {
            this.id = id;
            this.random = new Random();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000); // 顧客等待服務生的時間

                int orderQuantity = random.nextInt(MAX_ORDER_QUANTITY - MIN_ORDER_QUANTITY + 1) + MIN_ORDER_QUANTITY;
                String dumplingType = getRandomDumplingType();

                boolean success = placeOrder(dumplingType, orderQuantity);
                if (success) {
                    System.out.println("顧客 " + id + " 訂購了 " + orderQuantity + " 顆 " + dumplingType);
                } else {
                    System.out.println("顧客 " + id + " 訂購的 " + dumplingType + " 售完了");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private String getRandomDumplingType() {
            int randomNum = random.nextInt(3);
            switch (randomNum) {
                case 0:
                    return "豬肉水餃";
                case 1:
                    return "牛肉水餃";
                case 2:
                    return "蔬菜水餃";
                default:
                    return "";
            }
        }

        private boolean placeOrder(String dumplingType, int quantity) throws InterruptedException {
            synchronized (lock) {
                switch (dumplingType) {
                    case "豬肉水餃":
                        if (remainingPorkDumplings >= quantity) {
                            remainingPorkDumplings -= quantity;
                            return true;
                        }
                        break;
                    case "牛肉水餃":
                        if (remainingBeefDumplings >= quantity) {
                            remainingBeefDumplings -= quantity;
                            return true;
                        }
                        break;
                    case "蔬菜水餃":
                        if (remainingVegetableDumplings >= quantity) {
                            remainingVegetableDumplings -= quantity;
                            return true;
                        }
                        break;
                }
                return false;
            }
        }
    }
}

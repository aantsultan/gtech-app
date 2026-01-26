package id.task.gtech_app;

import org.junit.jupiter.api.Test;

class GtechAppApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(perkalianSederhana(100, 2));
    }

    public int perkalianSederhana(int j, int k) {
        int hasil = 0;
        while (j > 0) {
            hasil += k;
            j--;
        }
        return hasil;
    }

}

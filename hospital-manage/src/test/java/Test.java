import per.hp.hospitalmanager.util.MD5;

public class Test {
    public static void main(String[] args) {
        String encrypt = MD5.encrypt("6baa1634798ec0b3b7538a021b4ac160");
        System.out.println(encrypt);
    }
}

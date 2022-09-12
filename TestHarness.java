public class TestHarness {
    Scramblr SC = new Scramblr();

    public TestHarness(){
        
        
        SC.setFTEnc("test.txt");
        SC.Encrypt("test_out");
        
        
        SC.setFTDec("test_out.txt");
        SC.Decrypt("decrypt_out");
        
        
    }

    public static void main(String[] args) {
        new TestHarness();
    }
}

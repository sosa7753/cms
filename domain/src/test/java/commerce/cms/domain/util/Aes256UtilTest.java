package commerce.cms.domain.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Aes256UtilTest {

  @Test
  void encrypt() {
    String encrypt = Aes256Util.encrypt("Hello world");
    Assertions.assertEquals(Aes256Util.decrypt(encrypt), "Hello world");
  }
}
package trains;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Timeout;

@Timeout(value = 5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class MyTests {
    // Write your tests here
}

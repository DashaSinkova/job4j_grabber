import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class Test1Test {
    @Test
    public void whenReturn() {
        Assert.assertThat(Test1.returnInt(5), is(5));
    }
}

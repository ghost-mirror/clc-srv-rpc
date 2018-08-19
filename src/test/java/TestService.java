import com.ghotsmirror.cltsrvrpc.impl.ServiceContainer;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

public class TestService extends Assert {
    private static final Logger log = Logger.getLogger(TestService.class.getSimpleName());
    private ServiceContainer container;

    public static void main(String[] args) throws Exception {
        ServiceContainer container;
        log.info("TEST SERVER");
        try {
            container = new ServiceContainer("src/main/resources/service.properties");
        } catch (Exception e) {
            log.fatal(e);
            throw e;
        }

        try {
            log.info("ADD = " + container.getService("arithmetic").invoke("sum", new Object[]{5, 10}));
            log.info("ADD = " + container.getService("arithmetic").invoke("sum", new Object[]{new Integer(7), new Integer(12)}));
            log.info("SLP = " + container.getService("sleepy").invoke("sleep", new Object[]{5}));
            log.info("MUL = " + container.getService("arithmetic").invoke("mul", new Object[]{5, 10}));
            log.info("RND = " + container.getService("stupid").invoke("rnd", new Object[]{}));
            log.info("ADD = " + container.getService("stupid").invoke("sum", new Object[]{50, 10}));
        } catch (Exception e) {
            log.error(e);
        }

    }

    @Test
    @Before
    public void container() throws Exception  {
        container = new ServiceContainer("src/main/resources/service.properties");
    }
    @Test
    public void arithmetic_0() throws Exception  {
        assertEquals(15, container.getService("arithmetic").invoke("sum", new Object[]{5, 10}));
    }
    @Test
    public void arithmetic_1() throws Exception  {
        assertEquals(77, container.getService("arithmetic").invoke("mul", new Object[]{7, 11}));
    }
    @Test
    public void arithmetic_2() throws Exception  {
        assertEquals(4, container.getService("arithmetic").invoke("div", new Object[]{13, 3}));
    }
    @Test(expected = NoSuchMethodException.class)
    public void arithmetic_3() throws Exception  {
        assertEquals(77, container.getService("arithmetic").invoke("mul", new Object[]{7, 11, 11}));
    }
    @Test
    public void stupid_0() throws Exception  {
        container.getService("stupid").invoke("rnd", new Object[]{});
    }
    @Test(expected = NoSuchMethodException.class)
    public void stupid_1() throws Exception  {
        assertEquals(50, container.getService("stupid").invoke("mul", new Object[]{5, 10}));
    }
    @Test
    public void sleepy_0() throws Exception  {
        assertEquals(5, container.getService("sleepy").invoke("sleep", new Object[]{5}));
    }

}

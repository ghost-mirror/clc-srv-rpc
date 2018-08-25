import com.ghostmirror.cltsrvrpc.common.EServiceResult;
import com.ghostmirror.cltsrvrpc.impl.server.ServiceContainer;
import com.ghostmirror.cltsrvrpc.server.IServiceResult;
import org.apache.log4j.Logger;
import org.junit.Before;
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
            log.info("ADD = " + container.getService("arithmetic").invoke("sum", new Object[]{5, 10}).getObject());
            log.info("ADD = " + container.getService("arithmetic").invoke("sum", new Object[]{7, 12}).getObject());
            log.info("SLP = " + container.getService("sleepy").invoke("sleep", new Object[]{5}).getObject());
            log.info("MUL = " + container.getService("arithmetic").invoke("mul", new Object[]{5, 10}).getObject());
            log.info("RND = " + container.getService("stupid").invoke("rnd", new Object[]{}).getObject());
            log.info("DAT = " + container.getService("sleepy").invoke("currentDate", new Object[]{}).getObject());
            log.info("ADD = " + container.getService("stupid").invoke("sum", new Object[]{50, 10}).getObject());
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
    public void add_0() {
        IServiceResult result = container.getService("add").invoke("sum", new Object[]{5, 10});
        assertSame(result.getType(), EServiceResult.WrongService);
    }
    @Test
    public void arithmetic_0() {
        IServiceResult result = container.getService("arithmetic").invoke("sum", new Object[]{5, 10});
        assertSame(result.getType(), EServiceResult.RESULT);
        assertEquals(15, result.getObject());
    }
    @Test
    public void arithmetic_1() {
        IServiceResult result = container.getService("arithmetic").invoke("mul", new Object[]{7, 11});
        assertSame(result.getType(), EServiceResult.RESULT);
        assertEquals(77, result.getObject());
    }
    @Test
    public void arithmetic_2() {
        IServiceResult result = container.getService("arithmetic").invoke("div", new Object[]{13, 3});
        assertSame(result.getType(), EServiceResult.RESULT);
        assertEquals(4, result.getObject());
    }
//    @Test(expected = NoSuchMethodException.class)
    @Test
    public void arithmetic_3() {
        IServiceResult result = container.getService("arithmetic").invoke("mul", new Object[]{7, 11, 11});
        assertSame(result.getType(), EServiceResult.WrongParameters);
    }
    @Test
    public void stupid_0() {
        IServiceResult result = container.getService("stupid").invoke("rnd", new Object[]{});
        assertSame(result.getType(), EServiceResult.RESULT);
    }
    @Test
    public void stupid_1() {
        IServiceResult result = container.getService("stupid").invoke("mul", new Object[]{5, 10});
        assertSame(result.getType(), EServiceResult.WrongMethod);
    }
    @Test
    public void stupid_2() {
        IServiceResult result = container.getService("stupid").invoke("nothing", new Object[]{});
        assertSame(result.getType(), EServiceResult.VOID);
    }
    @Test
    public void sleepy_0() {
        IServiceResult result = container.getService("sleepy").invoke("sleep", new Object[]{});
        assertSame(result.getType(), EServiceResult.WrongParameters);
    }
    @Test
    public void sleepy_1() {
        IServiceResult result = container.getService("sleepy").invoke("sleep", new Object[]{5});
        assertSame(result.getType(), EServiceResult.VOID);
    }
    @Test
    public void sleepy_2() {
        IServiceResult result = container.getService("sleepy").invoke("currentDate", new Object[]{});
        assertSame(result.getType(), EServiceResult.RESULT);
    }
}

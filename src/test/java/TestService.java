import com.ghotsmirror.cltsrvrpc.core.WrongMethod;
import com.ghotsmirror.cltsrvrpc.core.WrongParametrs;
import com.ghotsmirror.cltsrvrpc.core.WrongService;
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
            log.info("ADD = " + container.getService("arithmetic").invoke("sum", new Object[]{5, 10}).getObject());
            log.info("ADD = " + container.getService("arithmetic").invoke("sum", new Object[]{new Integer(7), new Integer(12)}).getObject());
            log.info("SLP = " + container.getService("sleepy").invoke("sleep", new Object[]{5}).getObject());
            log.info("MUL = " + container.getService("arithmetic").invoke("mul", new Object[]{5, 10}).getObject());
            log.info("RND = " + container.getService("stupid").invoke("rnd", new Object[]{}).getObject());
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
    public void add_0() throws Exception  {
        assertEquals(true, container.getService("add").invoke("sum", new Object[]{5, 10}).getObject() instanceof WrongService);
    }
    @Test
    public void arithmetic_0() throws Exception  {
        assertEquals(15, container.getService("arithmetic").invoke("sum", new Object[]{5, 10}).getObject());
    }
    @Test
    public void arithmetic_1() throws Exception  {
        assertEquals(77, container.getService("arithmetic").invoke("mul", new Object[]{7, 11}).getObject());
    }
    @Test
    public void arithmetic_2() throws Exception  {
        assertEquals(4, container.getService("arithmetic").invoke("div", new Object[]{13, 3}).getObject());
    }
//    @Test(expected = NoSuchMethodException.class)
    @Test
    public void arithmetic_3() throws Exception  {
        assertEquals(true, container.getService("arithmetic").invoke("mul", new Object[]{7, 11, 11}).getObject() instanceof WrongParametrs);
    }
    @Test
    public void stupid_0() throws Exception  {
        container.getService("stupid").invoke("rnd", new Object[]{});
    }
    @Test
    public void stupid_1() throws Exception  {
        assertEquals(true, container.getService("stupid").invoke("mul", new Object[]{5, 10}).getObject() instanceof WrongMethod);
    }
    @Test
    public void stupid_2() throws Exception  {
        assertEquals(false, container.getService("stupid").invoke("mul", new Object[]{5, 10}).isVoid());
    }
    @Test
    public void stupid_3() throws Exception  {
        assertEquals(true, container.getService("stupid").invoke("nothing", new Object[]{}).isVoid());
    }
    @Test
    public void sleepy_0() throws Exception  {
        assertEquals(5, container.getService("sleepy").invoke("sleep", new Object[]{5}).getObject());
    }

}

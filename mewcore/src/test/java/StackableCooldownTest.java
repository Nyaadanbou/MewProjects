import cc.mewcraft.mewcore.cooldown.StackableCooldown;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.time.Time;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StackableCooldownTest {

    static StackableCooldown cooldown;
    static long stacks;

    static void printRemaining() {
        System.out.println("RemainingTime: " + cooldown.remainingTime(TimeUnit.MILLISECONDS));
        System.out.println("RemainingTimeAll: " + cooldown.remainingTimeAll(TimeUnit.MILLISECONDS));
        System.out.println("UsableStacks: " + cooldown.getAvailable());
        System.out.println();
    }

    @BeforeAll
    static void beforeAll() {
        stacks = 5;
        cooldown = StackableCooldown.of(Cooldown.of(200, TimeUnit.MILLISECONDS), () -> stacks);

        // simulate that all stacks are ready to use at the beginning
        cooldown.setLastTested(Time.nowMillis() - stacks * cooldown.getBaseTimeout());
    }

    @Test
    @Order(1)
    void test1() {
        printRemaining();
        for (int i = 0; i < stacks; i++) {
            assertTrue(cooldown.test());
        }
        printRemaining();
    }

    @Test
    @Order(2)
    void test2() {
        printRemaining();
        assertFalse(cooldown.test());
        printRemaining();
    }

    @Test
    @Order(3)
    void test3() throws InterruptedException {
        Thread.sleep(200L);
        printRemaining();
        assertTrue(cooldown.test());
        printRemaining();
    }

    @Test
    @Order(4)
    void test4() throws InterruptedException {
        Thread.sleep(100L);
        printRemaining();
        assertFalse(cooldown.test());
        printRemaining();
    }

    @Test
    @Order(5)
    void test5() throws InterruptedException {
        Thread.sleep(200L * 2);
        printRemaining();
        assertTrue(cooldown.test());
        assertTrue(cooldown.test());
        assertFalse(cooldown.test());
        assertFalse(cooldown.test());
        assertFalse(cooldown.test());
        printRemaining();
    }

    @Test
    @Order(6)
    void test6() throws InterruptedException {
        printRemaining();
        Thread.sleep(200L);
        assertTrue(cooldown.test());
        assertFalse(cooldown.test());
    }

    @Test
    @Order(7)
    void test7() throws InterruptedException {
        printRemaining();
        Thread.sleep(200L * 5);
        printRemaining();
        for (int i = 0; i < stacks; i++) {
            assertTrue(cooldown.test());
        }
        printRemaining();
    }

    @Test
    @Order(8)
    void test8() {
        assertFalse(cooldown.test());
    }

    @Test
    @Order(9)
    void testAddBalance() throws InterruptedException {
        stacks += 1; // change the balance: 5 -> 6
        Thread.sleep(200L * stacks);
        for (int i = 0; i < stacks; i++) {
            printRemaining();
            assertTrue(cooldown.test());
        }
    }

    @Test
    @Order(10)
    void test10() throws InterruptedException {
        printRemaining();
        Thread.sleep(100L);
        assertFalse(cooldown.test());
        Thread.sleep(100L);
        assertTrue(cooldown.test());
        assertFalse(cooldown.test());
    }

    @Test
    @Order(11)
    void testRemoveBalance() throws InterruptedException {
        stacks = 2;
        Thread.sleep(200L * 2);
        assertTrue(cooldown.test());
        assertTrue(cooldown.test());
        assertFalse(cooldown.test());
    }
}

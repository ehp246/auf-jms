package me.ehp246.aufjms.core.endpoint;

import org.junit.jupiter.api.Disabled;

/**
 * @author Lei Yang
 *
 */
@Disabled
class ForJmsScannerTest {
    /*
     * @Test void case001() { final var found = new
     * ForJmsScanner(Set.of(Case001.class.getPackageName())).perform();
     * 
     * Assertions.assertEquals(1, found.size());
     * 
     * final var one = found.stream().findAny().get();
     * 
     * Assertions.assertEquals(Case001.class, one.getInstanceType());
     * Assertions.assertEquals(Case001.class.getSimpleName(), one.getMsgType());
     * Assertions.assertEquals(InstanceScope.MESSAGE, one.getInstanceScope());
     * Assertions.assertEquals(InvocationModel.DEFAULT, one.getInvocationModel());
     * 
     * final var methods = one.getMethods();
     * 
     * Assertions.assertEquals(4, methods.size());
     * 
     * final var reflected = new ReflectingType<>(Case001.class);
     * 
     * Assertions.assertEquals(reflected.findMethod("m001"), methods.get("m001"));
     * Assertions.assertEquals(reflected.findMethod("m003"), methods.get("m003"));
     * Assertions.assertEquals(reflected.findMethod("m001", int.class),
     * methods.get("m001-1")); Assertions.assertEquals(reflected.findMethod("m002",
     * int.class), methods.get("m002"));
     * 
     * Assertions.assertEquals(false,
     * methods.containsValue(reflected.findMethod("m002")),
     * "Should not have the un-annotated"); }
     * 
     * @Test void duplicateDefaults001() {
     * Assertions.assertThrows(RuntimeException.class, () -> new
     * ForJmsScanner(Set.of(Case002.class.getPackageName())).perform()); }
     * 
     * @Test void duplicateInvokingNames001() {
     * Assertions.assertThrows(RuntimeException.class, () -> new
     * ForJmsScanner(Set.of(Case003.class.getPackageName())).perform()); }
     * 
     * @Test void missingInvoking001() {
     * Assertions.assertThrows(RuntimeException.class, () -> new
     * ForJmsScanner(Set.of(Case004.class.getPackageName())).perform()); }
     * 
     * @Test void scope001() { Assertions.assertThrows(RuntimeException.class, () ->
     * new ForJmsScanner(Set.of(Case005.class.getPackageName())).perform()); }
     * 
     * @Test void scope002() { final var found = new
     * ForJmsScanner(Set.of(Case006.class.getPackageName())).perform();
     * 
     * Assertions.assertEquals(1, found.size());
     * 
     * final var one = found.stream().findAny().get();
     * 
     * Assertions.assertEquals(Case006.class, one.getInstanceType());
     * Assertions.assertEquals(Case006.class.getSimpleName(), one.getMsgType());
     * Assertions.assertEquals(InstanceScope.BEAN, one.getInstanceScope());
     * Assertions.assertEquals(InvocationModel.DEFAULT, one.getInvocationModel()); }
     */
}

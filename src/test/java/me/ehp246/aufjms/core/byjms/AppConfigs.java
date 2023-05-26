package me.ehp246.aufjms.core.byjms;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.core.byjms.registrar.case01.RegistrarCase01;
import me.ehp246.aufjms.core.byjms.registrar.case02.RegistrarCase02;

/**
 * @author Lei Yang
 *
 */
class AppConfigs {
    @EnableByJms
    static class Config01 {

    }

    @EnableByJms(scan = { RegistrarCase01.class })
    static class Config02 {

    }

    @EnableByJms(scan = { RegistrarCase02.class })
    static class Config03 {

    }

    @EnableByJms
    static class DispatchFnConfig01 {
    }

    @EnableByJms(dispatchFns = "")
    static class DispatchFnConfig02 {
    }

    @EnableByJms(dispatchFns = { "1", "2" })
    static class DispatchFnConfig03 {
    }
}

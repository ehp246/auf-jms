package me.ehp246.aufjms.core.byjms.registrar;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.core.byjms.registrar.case01.RegistrarCase01;
import me.ehp246.aufjms.core.byjms.registrar.case02.RegistrarCase02;

/**
 * @author Lei Yang
 *
 */
public class RegistrarAppConfigs {
    @EnableByJms
    public static class Config01 {

    }

    @EnableByJms(scan = { RegistrarCase01.class })
    public static class Config02 {

    }

    @EnableByJms(scan = { RegistrarCase02.class })
    public static class Config03 {

    }

    @EnableByJms(scan = {
            RegistrarCase01.class })
    public static class ReplyToConfig01 {

    }

    @EnableByJms(scan = {
            RegistrarCase01.class })
    public static class DestinationConfig01 {

    }

    @EnableByJms(scan = { RegistrarCase02.class })
    public static class DestinationConfig02 {

    }

    @EnableByJms(scan = {
            RegistrarCase01.class }, ttl = "PT0.11S")
    public static class TtlConfig01 {

    }

    @EnableByJms(scan = {
            RegistrarCase02.class }, ttl = "PT0.112S")
    public static class TtlConfig02 {

    }
}

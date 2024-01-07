/* SecuDroid  - An open source, free manager for SECU-3 engine control unit
   Copyright (C) 2020 Vitaliy O. Kosharskiy. Ukraine, Kharkiv

   SECU-3  - An open source, free engine control unit
   Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Kyiv

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

   contacts:
              http://secu-3.org
              email: vetalkosharskiy@gmail.com
*/
package org.secu3.android.models.packets

data class SensorsPacket(
    var frequen: Int = 0,           //частота вращения коленвала двигателя
    var pressure: Float = 0f,       //давление во впускном коллекторе
    var voltage: Float = 0f,        //напряжение бортовой сети
    var temperature: Float = 0f,    //Температура охлаждающей жидкости
    var advAngle: Float = 0f,       //Текущий УОЗ (число со знаком)
    var knockValue: Float = 0f,     //Уровень детонации двигателя
    var knockRetard: Float = 0f,    //Корректировка УОЗ при детонации
    var airflow: Int = 0,           //Расход воздуха
    var sensorsFlags: Int = 0,      //16 bit flags 
    var tps: Float = 0f,            // TPS throttle position sensor (0...100%, x2)
    var addI1: Float = 0f,          // ADD_I1 voltage
    var addI2: Float = 0f,          // ADD_I2 voltage
    var ecuErrors: Int = 0,            // Check Engine errors
    var chokePosition: Float = 0f,     //
    var gasDosePosition: Int = 0,      // gas dosator position
    private var rawSpeed: Float = 0f,     // vehicle speed (2 bytes)
    private var rawDistance: Float = 0f,  // distance (3 bytes)
    var fuelInject: Float = 0f,           // instant fuel flow (frequency: 16000 pulses per 1L of burnt fuel)
    var airTempSensor: Float = 0f,     // 0x7FFF indicates that it is not used, voltage will be shown on the dashboard
    //corrections
    var strtAalt: Int = 0,  // advance angle from start map
    var idleAalt: Int = 0,  // advance angle from idle map
    var workAalt: Int = 0,  // advance angle from work map
    var tempAalt: Int = 0,  // advance angle from coolant temperature correction map
    var airtAalt: Int = 0,  // advance angle from air temperature correction map
    var idlregAac: Int = 0, // advance angle correction from idling RPM regulator
    var octanAac: Int = 0,  // octane correction value

    var lambdaCorr: Float = 0f,    // lambda correction = (lambda_corr/512.0f * 100.0f)
    var injPw: Float = 0f,     // injector pulse width = inj_pw/3.2f / 1000.0f
    var tpsdot: Int = 0,    // TPS opening/closing speed
    // if SECU-3T
    var map2: Float = 0f,
    var mapDiff: Float = 0f,
    var tmp2: Float = 0f,
    var afr: Float = 0f,    // #if defined(FUEL_INJECT) || defined(CARB_AFR) || defined(GD_CONTROL)
    var load: Float = 0f,
    var baroPress: Float = 0f,
    var iit: Short = 0,     //inj timing combine inj.timing (value*16)
    // and information about inj.timing mode (begin, middle or end of pulse)
    var rigidArg: Int = 0,
    // version > 4.8
    var grts: Float = 0f,      // fas reducer's temperature
    var rxlaf: Int = 0,     // RxL air flow, note: it is divided by 32
    var ftls: Float = 0f,      //fuel tank level
    var egts: Float = 0f,      //exhaust gas temperature
    var ops: Float = 0f,       //oil pressure
    var injDuty: Float = 0f,       // injector`s duty in % (value * 2)
    var maf: Float = 0f,           // Air flow (g/sec) * 64 from the MAF Sensor
    var ventDuty: Float = 0f,      //PWM duty of cooling fan
    var unioutStates: Int = 0,  //status of universal outputs
    var mapDot: Int = 0,        //Map dot
    var fts: Float = 0f,            //fuel temperature
    var consFuel: Float = 0f,
    var lambdaCorr2: Float = 0f,
    var afr2: Float = 0f,
    var lambdaMx: Float = 0f

) : BaseSecu3Packet() {

    fun getSpeed(): Float {
        var speed = 0f

        speed = rawSpeed / 32.0f

        if (speed >= 999.9f) {
            speed = 999.9f
        }

       /* if (rawSpeed != 0 && rawSpeed != 65535) { //speed sensor is used, value is correct

            val periodS = rawSpeed / 312500f      //period in seconds

            speed = ((periodDistance / periodS) * 3600.0f) / 1000.0f; //Km/h

            if (speed >= 999.9f) {
                speed = 999.9f
            }
        }*/

        return speed
    }

    fun getDistance(): Float {
        //var distance = (periodDistance * rawDistance) / 1000.0f
        var distance = rawDistance/125.0f

        if (distance > 99999.99f) {
            distance = 99999.99f
        }

        return distance
    }

    val gasBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_GAS)

    val ephhValveBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_EPHH_VALVE)

    val epmValveBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_EPM_VALVE)

    val coolFanBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_COOL_FAN)

    val checkEngineBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_CE_STATE)

    val carbBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_CARB)

    val stBlockBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_ST_BLOCK)

    val accelerationBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_ACCELERATION)

    val fcRevlimBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_FC_REVLIM)

    val floodClearBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_FLOODCLEAR)

    val sysLockedBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_SYS_LOCKED)

    val ignIBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_IGN_I)

    val condIBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_COND_I)

    val epasIBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_EPAS_I)

    val afterStEnrBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_AFTSTR_ENR)

    val iacClosedLoopBit: Int
        get() = sensorsFlags.getBitValue(BITNUMBER_IAC_CLOSED_LOOP)

    // UniOut States of universal outputs
    val uniOut0Bit: Int
        get() = unioutStates.getBitValue(BITNUMBER_UNIOUT0)

    val uniOut1Bit: Int
        get() = unioutStates.getBitValue(BITNUMBER_UNIOUT1)

    val uniOut2Bit: Int
        get() = unioutStates.getBitValue(BITNUMBER_UNIOUT2)

    val uniOut3Bit: Int
        get() = unioutStates.getBitValue(BITNUMBER_UNIOUT3)

    val uniOut4Bit: Int
        get() = unioutStates.getBitValue(BITNUMBER_UNIOUT4)

    val uniOut5Bit: Int
        get() = unioutStates.getBitValue(BITNUMBER_UNIOUT5)


    companion object {

        internal const val DESCRIPTOR = 'q'
        internal const val PACKET_SIZE = 98 //размер пакета без сигнального символа,
        // дескриптора и символа-конца пакета

        fun parse(data: String) = SensorsPacket().apply {
            frequen = data.get2Bytes(2)
            pressure = data.get2Bytes(4).toFloat() / MAP_MULTIPLIER
            voltage = data.get2Bytes(6).toFloat() / UBAT_MULTIPLIER
            temperature = data.get2Bytes(8).toShort().toFloat() / TEMPERATURE_MULTIPLIER
            advAngle = data.get2Bytes(10).toShort().toFloat() / ANGLE_DIVIDER
            knockValue = data.get2Bytes(12).toFloat() / ADC_MULTIPLIER
            //knockRetard = data.get2Bytes(14).toFloat()/ ANGLE_DIVIDER
            data.get2Bytes(14).takeIf { it != 0x7FFF }?.let{
                knockRetard = it.toFloat() / ANGLE_DIVIDER
            }
            airflow = data[16].code
            sensorsFlags = data.get2Bytes(17)
            tps = data[19].code.toFloat() / TPS_MULTIPLIER
            addI1 = data.get2Bytes(20).toFloat() / UBAT_MULTIPLIER
            addI2 = data.get2Bytes(22).toFloat() / UBAT_MULTIPLIER
            ecuErrors = data.get4Bytes(24)
            chokePosition = data[28].toFloat() / CHOKE_MULTIPLIER
            gasDosePosition = data[29].toInt() / GAS_DOSE_MULTIPLIER
            rawSpeed = data.get2Bytes(30).toFloat()
            rawDistance = data.get3Bytes(32).toFloat()
            fuelInject = data.get2Bytes(35).toFloat() / 256f

            /**
             * sensorDat.inj_fff = ((float)inj_fff) / 256.0f; //because raw value multiplied by 256

            //calculate value of fuel flow in L/100km
            if (sensorDat.speed > .0f)
            sensorDat.inj_ffd = (sensorDat.inj_fff / sensorDat.speed) * ((3600.0f * 100.0f) / ((float)m_fffConst));
            else
            sensorDat.inj_ffd = .0f;

            sensorDat.inj_ffh = (3600.0f * sensorDat.inj_fff) / ((float)m_fffConst); //consumption in L/h*/

            data.get2Bytes(37).takeIf { it != 0x7FFF }?.let {
                airTempSensor = it.toShort().toFloat() / TEMPERATURE_MULTIPLIER
            }
            //strtAalt = data.get2Bytes(39)
            data.get2Bytes(39).takeIf { it != 0x7FFF }?.let {
                strtAalt = it
            }
            //idleAalt = data.get2Bytes(41)
            data.get2Bytes(41).takeIf { it != 0x7FFF }?.let {
                idleAalt = it
            }

            //workAalt = data.get2Bytes(43)
            data.get2Bytes(43).takeIf { it != 0x7FFF }?.let {
                workAalt = it
            }
            tempAalt = data.get2Bytes(45)

            //airtAalt = data.get2Bytes(47)
            data.get2Bytes(47).takeIf { it != 0x7FFF }?.let {
                airtAalt = it
            }

            //idlregAac = data.get2Bytes(49)
            data.get2Bytes(49).takeIf { it != 0x7FFF }?.let {
                idlregAac = it
            }

            octanAac = data.get2Bytes(51)
            lambdaCorr = (((data.get2Bytes(53).toFloat()) / 512.0f) * 100.0f) // Lambda correction value (signed value)
            injPw = ((data.get2Bytes(55) * 3.2f) / 1000.0f) //Injector pulse width (ms)
            tpsdot = data.get2Bytes(57) //TPS opening/closing speed (d%/dt = %/s), signed value
            map2 = data.get2Bytes(59).toFloat() / MAP_MULTIPLIER // давление газа
            mapDiff = (map2-pressure) //calculate here differential pressure
            tmp2 = data.get2Bytes(61).toShort().toFloat() / TEMPERATURE_MULTIPLIER //темп. газа
            afr = data.get2Bytes(63).toFloat() / AFR_MULTIPLIER
            load = data.get2Bytes(65).toFloat() / LOAD_PHYSICAL_MAGNITUDE_MULTIPLIER
            baroPress = data.get2Bytes(67).toFloat() / MAP_MULTIPLIER
            iit = data.get2Bytes(69).toShort() //inj.timing with info
            /**
             * int mode = (iit >> 14) & 0x3;
            float inj_timing = ((float)(iit & 0x3FFF)) / 16.0f; //inj.timing in crankshaft degrees
            float inj_pw_degr = (((360.0f/(1000.0f*60.0f))* sensorDat.frequen) * sensorDat.inj_pw); //inj. PW in crankshaft degrees
            if (mode == 0)
            { //begin
            sensorDat.inj_tim_begin = inj_timing;
            sensorDat.inj_tim_end = inj_timing - inj_pw_degr;
            }
            else if (mode == 1)
            { //middle
            sensorDat.inj_tim_begin = inj_timing + (inj_pw_degr / 2);
            sensorDat.inj_tim_end = inj_timing - (inj_pw_degr / 2);
            }
            else
            {//end
            sensorDat.inj_tim_begin = inj_timing + inj_pw_degr;
            sensorDat.inj_tim_end = inj_timing;
            }
            if (sensorDat.inj_tim_begin > 720.0f)
            sensorDat.inj_tim_begin = sensorDat.inj_tim_begin - 720.f;
            if (sensorDat.inj_tim_end < 0)
            sensorDat.inj_tim_end = sensorDat.inj_tim_end + 720.0f;
             *
             *
             * */
            rigidArg = data[71].toInt() //idling regulator's rigidity
            /**
             *  sensorDat.rigid_use = (rigid_arg != 255);
                sensorDat.rigid_arg = sensorDat.rigid_use ? (1.0f + (((float)rigid_arg) / (256.0f/7.0f))) : 0;
             */
            grts = data.get2Bytes(72).toShort().toFloat() / TEMPERATURE_MULTIPLIER //Gas reducer's temperature
            rxlaf = data.get2Bytes(74) * 32       // RxL air flow, note: it is divided by 32
            ftls = data.get2Bytes(76). toFloat() / FTLS_MULT //level of fuel in the fuel tank
            egts = data.get2Bytes(78).toFloat() / EGTS_MULT //exhaust gas temperature
            ops = data.get2Bytes(80).toFloat()/ OPS_MULT //oil pressure
            injDuty = data[82].toInt().toFloat() / 2.0f //injector's duty
            maf = data.get2Bytes(83).toFloat() / MAFS_MULT //mass air flow (g/sec)
            ventDuty = data[85].code.toFloat() / 2.0f  //PWM duty of cooling fan
            unioutStates = data[86].code            //universal output's state flags
            if(data.length>87){
                mapDot = data.get2Bytes(87)    //MAPdot (dP/dt = kPa/s), signed value
                fts = data.get2Bytes(89).toShort().toFloat() / FTS_MULT //FTS (Fuel temparature sensor)
                consFuel = data.get3Bytes(91).toFloat() / 1024.0f //consumed fuel
                afr2 = data.get2Bytes(94).toShort().toFloat() / AFR_MULTIPLIER //AFR value #2
                lambdaCorr2 = (data.get2Bytes(96).toFloat() / 512.0f) * 100.0f // Lambda correction value #2 (signed value)
                lambdaMx = data.get2Bytes(98).toFloat() * ADC_MULTIPLIER //mixed voltages from two EGO sensors
            }
        }


        private const val BITNUMBER_EPHH_VALVE = 0  //IE flag
        private const val BITNUMBER_CARB = 1        //carb. limit switch flag
        private const val BITNUMBER_GAS = 2         //gas valve flag
        private const val BITNUMBER_EPM_VALVE = 3   //power valve flag
        private const val BITNUMBER_CE_STATE = 4    //CE Flag
        private const val BITNUMBER_COOL_FAN = 5    //cooling fan flag
        private const val BITNUMBER_ST_BLOCK = 6    //starter blocking flag
        private const val BITNUMBER_ACCELERATION = 7  // acceleration enrichment flag
        private const val BITNUMBER_FC_REVLIM = 8     // fuel cut rev.lim. flag
        private const val BITNUMBER_FLOODCLEAR = 9    // flood clear mode flag
        private const val BITNUMBER_SYS_LOCKED = 10  // system locked flag (immobilizer)
        private const val BITNUMBER_IGN_I = 11       // IGN_I flag
        private const val BITNUMBER_COND_I = 12      // COND_I flag
        private const val BITNUMBER_EPAS_I = 13      // EPAS_I flag
        private const val BITNUMBER_AFTSTR_ENR = 14  // after start enrichment flag
        private const val BITNUMBER_IAC_CLOSED_LOOP = 15  // after start enrichment flag

        private const val BITNUMBER_UNIOUT0 = 0  // uniout 0
        private const val BITNUMBER_UNIOUT1 = 1  // uniout 1
        private const val BITNUMBER_UNIOUT2 = 2  // uniout 2
        private const val BITNUMBER_UNIOUT3 = 3  // uniout 3
        private const val BITNUMBER_UNIOUT4 = 4  // uniout 4
        private const val BITNUMBER_UNIOUT5 = 5  // uniout 5
    }
}

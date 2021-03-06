package uis.brt.rulesengine;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import uis.brt.actuator.admin.ActuatorAdmin;
import uis.brt.context.ContextInformation;

import org.easyrules.annotation.Rule;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Priority;

@Rule//(name = "Pez calido", description = "Evalua la medici�n del termometro y si es menor a la permitida, devuelve una advertencia")
public class PezCalido implements PlatformRule {

	// valores fijos para esta regla
	private String AssociatedSensorType = "termometro"; //Tipo de sensor asociado a esta regla
	private String AssociatedActuatorType = "mensaje"; //Tipo de actuador asociado a esta regla
	private HashMap<String, Integer> previousvalue = new HashMap<String, Integer>(); // guardara la medicion anterior en forma -> IdDevice, value
	private String accion = ""; //mensaje que se envia a su respectivo actuador

	private HashMap<String, String> sensorInd;
	private List<ContextInformation> pond;
	private boolean exe;
	ActuatorAdmin actuator;

	public String getName() {
		return "Pez calido";
	}

	public String getDescription() {
		return "Evalua la medici�n del termometro y si es menor a la permitida, devuelve una advertencia";
	}
	@Priority
	public int getPriority() {
		return 0;
	}
	@Condition
	public boolean evaluate() {
		if(exe){
			Integer x = Integer.parseInt( sensorInd.get("value")); //valor actual a evaluar
			if(x != previousvalue.get( sensorInd.get("id").toString() )){ // comprobando la medicion actual VS la medicion anterior
				previousvalue.put(sensorInd.get("id").toString(), x); // agregando medicion anterior
				if(x <= 19) //evaluando si cumple la condicion para ejecutar un actuador
					exe = true;
				else
					exe = false;}
			else
				exe = false;
			}
		return exe;
	}
	@Action// solo se ejecutara, si el return del metodo evaluate, es true
	public void execute() throws Exception {
		String id =  sensorInd.get("id"); String type =  sensorInd.get("type");
		for (ContextInformation ins : pond) { // se busca en cada estanque del sistema
			if(ins.getDevices().containsValue(type) && ("calido".equals(ins.getWeather())) ){ // evalua si este estanque posee el mismo tipo de sensor que se evaluo en el metodo evaluate
				if(ins.getDevices().containsKey(id)){//  evalua si este estanque posee algun sensor con el mismo id que se evaluo en el metodo evaluate
					String IdOfActuatorToExecute = "";
					if(ins.getDevices().containsValue(AssociatedActuatorType)){ // evalua si este estanque posee algun actuador del mismo tipo de actuador asociado a esta regla
						for(Entry<String, String> x : ins.getDevices().entrySet()) // se busca en todos los devices asociados al estanque
							if(AssociatedActuatorType.equals(x.getValue().toString())){ // evalua si alguno de los devices del estanque existe un tipo que sea igual al tipo de esta regla
								IdOfActuatorToExecute = x.getKey().toString(); // asigna el id del device que es igual en la regla y en el estanque
								accion = "�������� PELIGRO: el agua del estanque " + ins.getElement() + " esta muy fria, poblacion en riesgo !!!!!!!!";
								actuator.executeActuators(true, IdOfActuatorToExecute, AssociatedActuatorType, accion);
							}}}}}
	}

	public void setData(HashMap<String, String> sensorInd, List<ContextInformation> pond, ActuatorAdmin actuator) {
		if(sensorInd.containsValue(AssociatedSensorType)){ // falta definir bien si se guarda como ahora: el tipo de device o se guarda es: la medicion
			this.sensorInd = sensorInd;
			this.pond = pond;
			this.actuator = actuator;
			exe = true;
		}
		else
			exe = false;
	}
	
}
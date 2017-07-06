package uis.brt.rulesengine;

import java.util.HashMap;
import java.util.List;

import org.easyrules.annotation.Rule;

import uis.brt.actuator.admin.ActuatorAdmin;
import uis.brt.context.ContextInformation;

import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Priority;

@Rule
public class PezFrio implements PlatformRule {

	private int medicion;
	private String pez = "";
	private String mensaje = "defecto";

	public String getName() {
		return "Pez frio";
	}

	public String getDescription() {
		return "Evalua la medici�n del termometro y de acuerdo a la temperatura devuelve una advertencia";
	}
	@Priority
	public int getPriority() {
		return 0;
	}
	@Condition
	public boolean evaluate() {
		Boolean eval = false;
		if(pez.equals("frio")){
			eval = true;
			if(medicion < 20 )
				mensaje = "EXCELENTE: la poblacion de peces frios esta en optimas condiciones";
			else
				mensaje = "PELIGRO: el agua esta demasiado caliente, poblacion en riesgo";
			}
		return eval;
	}
	@Action
	public void execute() throws Exception {
		System.out.println(mensaje);
	}

	public void setData(HashMap<String, String> map, List<ContextInformation> context, ActuatorAdmin actuator) {
		if(map.containsKey("tipo"))
			pez = (String) map.get("tipo");
		
		if(map.containsKey("Termometro"))
			this.medicion = Integer.parseInt(map.get("Termometro"));
	}
	
}
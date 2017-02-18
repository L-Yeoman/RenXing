package com.dldzkj.app.renxing.blelib.services;

import android.content.ContentResolver;
import android.content.Context;

public class ResolverSer {
   private ResolverSer resolverSer;
   
   public synchronized ResolverSer getResolverSer(){

	   if(resolverSer == null){
		   resolverSer = new ResolverSer();
	   }
	   return resolverSer;   
   }
   
   public  ContentResolver getResover( Context context){

	   ContentResolver s = context.getContentResolver();
	  return s;
   }
}

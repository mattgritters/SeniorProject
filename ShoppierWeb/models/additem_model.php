<?php

/**
 * This is basically a simple CRUD demonstration.
 */

class Additem_Model extends Model
{
    public $errors = array();
    
    public function __construct()
    {
        parent::__construct();
    }
    
     public function getAllStores()
    {
        
        $sth = $this->db->prepare("SELECT store_pk, store_name, store_type
                                           FROM store;");
        $sth->execute();    
                
        return json_encode($sth->fetchAll());
        
    }    
    public function getAisles($store)
    {
        $sth = $this->db->prepare("SELECT aisle_pk, aisle_name
                                           FROM aisle where aisle_strfk = :store;");
        $sth->execute(array(
            ':store' => $store));    
                
        return json_encode($sth->fetchAll());
    }
    public function getLocations($aisle)
    {
         $sth = $this->db->prepare("SELECT cat_pk, cat_name
                                           FROM category where cat_locfk = :aisle;");
        $sth->execute(array(
            ':aisle' => $aisle));    
                
        return json_encode($sth->fetchAll());

    }
    public function add($itemstr)
    {
        $split =  explode(",",$itemstr);

        $sth = $this->db->prepare("INSERT INTO item
                                   (item_catfk, item_name, item_brand)
                                   VALUES (:loc, :name, :brand);");
        $sth->execute(array(
            ':loc'   => $split[0],
            ':name'  => $split[1],
            ':brand' => $split[2]));   
        
        $count =  $sth->rowCount();
        if ($count == 1) {
            return true;
        } else {
            $this->errors[] = FEEDBACK_NOTE_CREATION_FAILED;
            return false;
        }
    }
    
}
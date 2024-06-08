<?php

class ImemDb{
    private static string $ip = "192.168.245.195";
    // private static string $ip = "127.0.0.1";
    private static int $port = 5346;

    public static function get(string $key): string|null{
     
        try{
            $socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP) or throw new Exception("Could not create socket");

            $result = socket_connect($socket, ImemDb::$ip, ImemDb::$port) or throw new Exception("Could not connect to server");
            $map = array(
                'key' => $key,
                'value' => ''
            );

            $json = json_encode($map)."\r\n";
             $msg = "/get" ."-ITaTI-".$json."\r\n";
    
            socket_write($socket, $msg, strlen($msg)) or throw new Exception("Could not send data to server");
            // socket_send($socket, $json."\r\n", strlen($json."\r\n"), MSG_EOF) or die("Could not send data to server\n");
 
            $result = socket_read ($socket, 1024) or throw new Exception("Could not read server response");

            $response = json_decode($result);
            socket_close($socket);
            if($response == null) return null;

            if($response->status == 200){
                
                return $response->data;
            }
            
            return null;
            
        }catch(Exception $e){
            throw new Exception($e->getMessage());
        }
    }

    public static function put(string $key, string $value): bool{
        try{
            $socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP) or throw new Exception("Could not create socket");

            $result = socket_connect($socket, ImemDb::$ip, ImemDb::$port) or throw new Exception("Could not connect to server");
            $map = array(
                'key' => $key,
                'value' => $value
            );

            $json = json_encode($map)."\r\n";
             $msg = "/put" ."-ITaTI-".$json."\r\n";
    
            socket_write($socket, $msg, strlen($msg)) or throw new Exception("Could not send data to server");
 
            $result = socket_read ($socket, 1024) or throw new Exception("Could not read server response");

            $response = json_decode($result);
            socket_close($socket);
            
            if($response == null) return false;

            if($response->status == 200){
                return true;
            }
            
            return false;
            
        }catch(Exception $e){
            throw new Exception($e->getMessage());
        }
    } 
}
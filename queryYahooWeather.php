<?php
    if(!empty($_GET))
    {
        $location = $_GET["locationValue"]; 
        $tempUnit = $_GET["tempUnit"];
        $locationType = $_GET["locationType"];
        $woeid = '';
        $request = '';

        $appid = "u.rQECDV34EsR7b2uFPnZgATEXPc6roj7D8LqoLI7Ap.1jwyVu_4QQAhM4WsaBrk_g--";
    
        if($locationType === 'zip')
        {
            $request = "http://where.yahooapis.com/v1/concordance/usps/" . $location . "?appid=" . $appid;
            if(!@simplexml_load_file($request))
            {
                //echo '<div align = "center"><h2>Zero results found!</h2></div>';
            }
            else
            {
                $woeidXML = @simplexml_load_file($request);
                if($woeidXML)
                {
                    $woeid = $woeidXML -> woeid;
                }
            }
        }
        
        else if($locationType === 'city')
        {
            if(!@simplexml_load_file("http://where.yahooapis.com/v1/places\$and(.q('" . urlencode($location) . "'),.type(7));start=0;count=5?appid=" . $appid))
            {
                //echo '<div align = "center"><h2>Zero results found!</h2></div>';
            }
            else
            {
                $woeidXML = @simplexml_load_file("http://where.yahooapis.com/v1/places\$and(.q('" . urlencode($location) . "'),.type(7));start=0;count=5?appid=" . $appid);
                if($woeidXML != NULL)
                {
                    $woeidList = array();
                    foreach($woeidXML->place as $key => $value)
                    {
                        $woeidList[] = $value->woeid;
                    }
                    //echo count($woeidList);
                    //print_r($woeidXML);
                    //if(count($woeidList) === 0) {?>
                        <!--<div align = "center"><h2>Zero results found!</h2></div>  -->
                    <?php  // } 
                    if($woeidList != NULL)
                    {
                        $woeid = $woeidList[0];
                    }    
                }  
            }        
        }
       // echo $woeid;
        if(strcmp($woeid, '') != 0)
        {
            $request = "http://weather.yahooapis.com/forecastrss?w=" . $woeid . "&u=" . $tempUnit;
            $forecastXML = simplexml_load_file($request);

            if($forecastXML)
            {
                $checkForError = $forecastXML->channel->item;
                if(strcmp($checkForError->title,'City not found') != 0)
                {
                    if($forecastXML->channel->item->children('http://www.w3.org/2003/01/geo/wgs84_pos#'))    
                    {
                        $geoNode = $forecastXML->channel->item->children('http://www.w3.org/2003/01/geo/wgs84_pos#');
                        
                        // GET LATITUDE/LONGITUDE DETAILS
                        if(empty($geoNode->lat))
                            $latitude = 'N/A';
                        else
                            $latitude = $geoNode->lat;
                        
                        if(empty($geoNode->long))
                            $longitude = 'N/A';
                        else    
                            $longitude = $geoNode->long;
                    }

                    if($forecastXML->channel->children('http://xml.weather.yahoo.com/ns/rss/1.0'))
                    {
                        $yweatherNode = $forecastXML->channel->children('http://xml.weather.yahoo.com/ns/rss/1.0');
                    
                        // GET LOCATIION DETAILS
                        if(empty($yweatherNode->location->attributes()->city))
                            $city = 'N/A';
                        else    
                            $city = $yweatherNode->location->attributes()->city; 
                            
                        if(empty($yweatherNode->location->attributes()->region))
                            $region = 'N/A';
                        else    
                            $region = $yweatherNode->location->attributes()->region; 
                            
                        if(empty($yweatherNode->location->attributes()->country))
                            $country = 'N/A';    
                        else    
                            $country = $yweatherNode->location->attributes()->country;
                                                    
                        // GET WEATHER DESCRIPTION
                        if(empty($yweatherNode->units->attributes()->temperature))
                            $units = 'N/A';
                        else
                            $units = $yweatherNode->units->attributes()->temperature;
                    }
                    if($forecastXML->channel->item->children('http://xml.weather.yahoo.com/ns/rss/1.0'))
                    {
                        $yweatherNodeCondition = $forecastXML->channel->item->children('http://xml.weather.yahoo.com/ns/rss/1.0');
                        if(empty($yweatherNodeCondition->condition->attributes()->temp))
                            $tempValue = 'N/A';
                        else
                            $tempValue = $yweatherNodeCondition->condition->attributes()->temp;
                            
                        if(empty($yweatherNodeCondition->condition->attributes()->text))
                            $tempCondition = 'N/A';
                        else    
                            $tempCondition = $yweatherNodeCondition->condition->attributes()->text;
                            
                        $yweatherNodeForecast = $forecastXML->channel->item->children('http://xml.weather.yahoo.com/ns/rss/1.0');
                        if(count($yweatherNodeForecast->forecast) != 0 )
                        {
                            foreach($yweatherNodeCondition->forecast as $forecast)
                            {
                                $forecastDay[] = $forecast->attributes()->day;
                                $forecastLow[] = $forecast->attributes()->low;
                                $forecastHigh[] = $forecast->attributes()->high;
                                $forecastText[] = $forecast->attributes()->text;
                            }
                        }    
                    }    
                    // GET LINK FOR DETAILS
                    if($forecastXML->channel->link)
                        $linkForDetails = $forecastXML->channel->link;
                    
                    // GET IMAGE SRC
                    $description = $forecastXML->channel->item->description;
                    $regexPattern = '/src="([^"]*)"/';
                    preg_match($regexPattern, $description, $image);
                    $src = $image[1];
                    
                    HttpResponse::setContentType('text/xml');
                    //$result = '';
                    echo  "<weather>";
                    echo "<feed>" . htmlentities($request) . "</feed>"; 
                    echo "<link>" . htmlentities($linkForDetails) . "</link>" ;
                    echo "<location city=\"" . $city . "\" region=\"" . $region . "\" country=\"" . $country . "\" />";
                    echo "<units temperature=\"" . $units . "\" />";
                    echo "<condition text=\"" . $tempCondition . "\" temp=\"" . $tempValue . "\" />";
                    echo "<img>" . $src . "</img>" ;
                    
                    for($i = 0; $i < count($forecastDay); $i++)
                    {
                        echo "<forecast day=\"" . $forecastDay[$i] . "\" low=\"" . $forecastLow[$i] . "\" high=\"" . $forecastHigh[$i] . "\" text=\"" . $forecastText[$i] . "\" />";
                    }
                    echo "</weather>";
                }
            }
        }
    }    
?>







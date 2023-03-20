import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import OtherAllVid from '../../Cards/Page/OtherAllVid';

import "./other.css";

const PageAllVideos = () => {
    const {state} = useLocation();
    const [vidList, setVidList] = useState([]);

    useEffect(() => {
        if(state){
            setVidList(state.vidList);
        }
    }, [state])

    function displayVid(){
        if(vidList != ''){
            return vidList.map((eachVid) => { 
                return <OtherAllVid key={eachVid.id} currVid={eachVid}/>
            })
        }
    }

    return (
        <div className='pageAllVid-Vid justify-content-center mt-3'>
            {displayVid()}
        </div>
    );
};

export default PageAllVideos;
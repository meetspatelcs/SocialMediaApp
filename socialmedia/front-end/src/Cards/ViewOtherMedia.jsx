import React, { useEffect, useState } from 'react';
import { Image } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';

import '../Components/Pages/other.css';

const ViewOtherMedia = (props) => {
    const user = useUser();
    const {pageId} = useParams();
    const {currImg} = props;
    const [myurl,setMyurl] = useState();
    const myPostId = currImg.id;
    
    // /api/pages/${pageId}/postImg/${myPostId}
    useEffect(() => {
        fetch(`/api/pagePosts/page/${pageId}/postImg/${myPostId}`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
            .then((dataBuff) => { return dataBuff.blob(); })
            .then((data) => {
                if(data.type.split("/")[1] != 'json'){ setMyurl(URL.createObjectURL(data)); }
                return data;
            })
            .catch((error) => {console.log(error);})
    }, [])

    function displayImg(){
        if(myurl != null && myurl != '' && myurl != undefined){
            return <Image src={myurl} className='otherTab-media' />
        }
    }

    return (
        <div>
            {displayImg()}
        </div>
    );
};

export default ViewOtherMedia;
import React, { useEffect, useState } from 'react';
import { Card } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import { useUser } from '../../UserProvider/UserProvider';

const OtherAllImg = (props) => {
    const user = useUser();
    const {pageId} = useParams();
    const {currImg} = props;
    const myPostId = currImg.id;

    const [myurl, setMyurl] = useState();

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
            return <Card.Img className='' src={myurl} />
        }
    }

    return (
        <Card className='d-flex align-items-center' style={{border: "none"}}>
            <Card.Body>
                {displayImg()}
            </Card.Body>
        </Card>
    );
};

export default OtherAllImg;
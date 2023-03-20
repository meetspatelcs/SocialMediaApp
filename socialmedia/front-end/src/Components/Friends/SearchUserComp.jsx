import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';

const SearchUserComp = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const {firstname, lastname, selectedFriendId} = props;
    const navigate = useNavigate();
    const default_Null_Status = null;
    const default_Friends = "Friends";
    const default_Pending = "Pending";

    const [isStatus, setIsStatus] = useState(false);
    const [compStatus, setCompStatus] = useState("");

    useEffect(()=>{
        requestToPath(`/api/friends/user/${selectedFriendId}/friendStatus`, "GET", user.jwt)
            .then((friendResponse) => {
                console.log(friendResponse);
                if(friendResponse.status == "Null"){
                    setCompStatus(default_Null_Status);
                }
                else if(friendResponse.status == default_Pending){
                    setCompStatus(default_Pending);
                }
                else if(friendResponse.status == default_Friends){
                    setCompStatus(default_Friends);
                }
                setIsStatus(true);
            })
            .catch((error) => {console.log(error);})
    },[selectedFriendId])

    function handleNavigate(){
        navigate(`/users/${userId}/user/${selectedFriendId}/profile`, {state:{compStatus}});
    }

    function viewBtn() {
        return <Button className='w-100 mt-1' onClick={handleNavigate}>View</Button>
    }

    function handleBtn(){
        return viewBtn();
    }

    function displayBody(){
        if(isStatus){
            return <Card.Body>
                        <Card.Title>{firstname} {lastname}</Card.Title>
                        {handleBtn()}
                   </Card.Body>
        }
    }

    return (
        <div>
            <Card className='d-flex flex-column'>
                {displayBody()}
            </Card>
        </div>
    );
};

export default SearchUserComp;
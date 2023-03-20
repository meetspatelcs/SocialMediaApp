import React, { useEffect, useRef, useState } from 'react';
import { Button, Spinner } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import FriendRequests from '../../Cards/FriendRequests';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';

import './friends.css';

function FriendRequestComp(props) {
    const user = useUser();
    const {compStatus} = props;
    const {userId} = useParams();

    const [isLoading, setIsLoading] = useState(false);
    const [friendList, setFriendList] = useState([]);

    const default_Received = 'Received';
    
    useEffect(() => {
        setIsLoading(true);
        requestToPath(`/api/friends`, "GET", user.jwt)
            .then((friendsData) => {
                setFriendList(friendsData);
                setIsLoading(false);
            })
            .catch((error) => {console.log(error);})
    }, []);

    function getPendingLength(){
        const myLength = friendList.filter(eachFriend => eachFriend.status === compStatus).length;
        return myLength;
    }

    function validateStatus(){
        if(friendList && getPendingLength() > 0){            
            return true;
        }
        else
            return false;
    }

    function handleLoading(){
       return <Button variant="secondary" disabled>
                <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true" />
                Loading...
            </Button>
    }

    function handleFriend(someVal){
        const copyVal = {...someVal};
        copyVal.status = "Friends";
        
        const listIndex = friendList.findIndex((eachFriend) => eachFriend.id == someVal.id);
        const copyFriendList = [...friendList];
        copyFriendList[listIndex] = copyVal;
        setFriendList(copyFriendList); 
    }

    return (
        <div> 
        { (isLoading) ? <>{handleLoading()}</> : 
            <>
                <div className='container friendLC-RC-wrapper mt-5 mb-5'>
                    <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Received</h2>
                    <div className="friendLC-RC-eachFriend justify-content-center">
                        {validateStatus() ? friendList.filter(eachFriend => 
                        eachFriend.status === compStatus && eachFriend.user.id != userId).sort((a,b) => b.id > a.id ? 1 : -1).map((eachFriend) => ( 
                            
                            <FriendRequests key={eachFriend} friendId={eachFriend.id} 
                                firstname={eachFriend.user.firstname} 
                                lastname={eachFriend.user.lastname} compStatus={default_Received}
                                selectedFriendId={eachFriend.user.id}
                                emitTest={handleFriend}/>
                            )) : <div>No data</div>
                        }
                    </div>
                </div>

                <div className='container friendLC-RC-wrapper mt-5 mb-5'>
                    <h2 className='ms-3 mb-3 px-2' style={{margin:"-1.7em", backgroundColor:"white", width:"min-content", whiteSpace: "nowrap"}}>Sent</h2>
                    <div className="friendLC-RC-eachFriend justify-content-center" >
                        {validateStatus() ? friendList.filter(eachFriend => 
                        eachFriend.status === compStatus && eachFriend.user.id == userId).sort((a,b) => b.id > a.id ? 1 : -1).map((eachFriend) => ( 
                            
                            <FriendRequests key={eachFriend} friendId = {eachFriend.id} 
                            firstname={eachFriend.requestedUser.firstname} 
                            lastname={eachFriend.requestedUser.lastname} compStatus={compStatus}
                            selectedFriendId={eachFriend.requestedUser.id}/>
                            )) 
                        : <div>No data</div>} 
                    </div>
                </div>
            </>
        }
        </div>
    );
}

export default FriendRequestComp;
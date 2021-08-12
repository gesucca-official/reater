package com.gsc.reater.service.shit.sub;

import com.gsc.reater.model.Network;
import com.gsc.reater.model.Node;
import com.gsc.reater.model.ReaterModel;

public interface SentenceBuildingService {

    boolean hasTerminalLinks(Node node, Network network);

    Node chooseEntryPoint(ReaterModel model);

    Node chooseNextToken(Node currentNode, ReaterModel model);

    Node chooseAmongTerminalLinks(Node node, Network network);

    void localizedAppend(Node currentNode, Node previousNode, StringBuilder sentence, SupportedLanguages lang);

}
